package io.github.hyuna.logmonitor.logging.service;

import io.github.hyuna.logmonitor.logging.controller.LogStreamController;
import io.github.hyuna.logmonitor.logging.dto.LogMessageRequest;
import io.github.hyuna.logmonitor.logging.dto.LogResponse;
import io.github.hyuna.logmonitor.logging.entity.LogEvent;
import io.github.hyuna.logmonitor.logging.enums.ErrorType;
import io.github.hyuna.logmonitor.logging.util.ErrorClassifier;
import io.github.hyuna.logmonitor.persistence.logging.repository.LogEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * LogService
 *
 * 역할:
 * - 로그 저장
 * - 로그 조회
 * - 로그 페이징 조회
 * - 로그 통계 조회
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogService {

    private final LogEventRepository repository;


    /**
     * 실시간 로그 스트림 전송 컨트롤러
     *
     * Kafka에서 수신한 로그 메시지를 WebSocket을 통해 실시간으로 클라이언트에 전달하는 역할
     */
    private final LogStreamController logStreamController;

    /**
     * 로그 저장
     *
     * Kafka Consumer에서 호출한다.
     */
    public void save(LogMessageRequest request) {

        ErrorType errorType = ErrorClassifier.classify(request.getMessage());

        LogEvent event = LogEvent.builder()
                .level(request.getLevel())
                .serviceName(request.getServiceName())
                .message(request.getMessage())
                .errorType(errorType.name())
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(event);
    }

    /**
     * 기존 조회 API 확장 버전
     *
     * 지원 조건:
     * - level
     * - serviceName
     * - keyword
     * - startDate
     * - endDate
     *
     * page 없이 전체 결과를 반환한다.
     */
    public List<LogResponse> getLogs(String level,
                                     String serviceName,
                                     String keyword,
                                     LocalDate startDate,
                                     LocalDate endDate) {

        String normalizedLevel = normalize(level);
        String normalizedServiceName = normalize(serviceName);
        String normalizedKeyword = normalizeKeyword(keyword);

        LocalDateTime startDateTime = toStartDateTime(startDate);
        LocalDateTime endDateTime = toEndDateTime(endDate);

        List<LogEvent> events = repository.searchLogs(
                normalizedLevel,
                normalizedServiceName,
                normalizedKeyword,
                startDateTime,
                endDateTime
        );

        return events.stream()
                .map(LogResponse::from)
                .toList();
    }

    /**
     * 기존 조회 API와의 호환을 위한 메서드
     */
    public List<LogResponse> getLogs(String level, String serviceName) {
        return getLogs(level, serviceName, null, null, null);
    }

    /**
     * 페이징 조회 확장 버전
     */
    public Page<LogResponse> getLogsWithPage(String level,
                                             String serviceName,
                                             String keyword,
                                             LocalDate startDate,
                                             LocalDate endDate,
                                             Pageable pageable) {

        String normalizedLevel = normalize(level);
        String normalizedServiceName = normalize(serviceName);
        String normalizedKeyword = normalizeKeyword(keyword);

        LocalDateTime startDateTime = toStartDateTime(startDate);
        LocalDateTime endDateTime = toEndDateTime(endDate);

        Page<LogEvent> events = repository.searchLogs(
                normalizedLevel,
                normalizedServiceName,
                normalizedKeyword,
                startDateTime,
                endDateTime,
                pageable
        );

        return events.map(LogResponse::from);
    }

    /**
     * 기존 페이징 조회 API와의 호환용 메서드
     */
    public Page<LogResponse> getLogsWithPage(String level,
                                             String serviceName,
                                             Pageable pageable) {
        return getLogsWithPage(level, serviceName, null, null, null, pageable);
    }

    /**
     * 로그 레벨별 통계 조회
     */
    public Map<String, Long> getLogStatsByLevel() {

        List<Object[]> results = repository.countLogsByLevel();

        Map<String, Long> stats = new HashMap<>();

        for (Object[] row : results) {
            String level = (String) row[0];
            Long count = (Long) row[1];
            stats.put(level, count);
        }

        return stats;
    }

    /**
     * 서비스명별 로그 통계 조회
     */
    public Map<String, Long> getLogStatsByServiceName() {

        List<Object[]> results = repository.countLogsByServiceName();

        Map<String, Long> stats = new HashMap<>();

        for (Object[] row : results) {
            String serviceName = (String) row[0];
            Long count = (Long) row[1];
            stats.put(serviceName, count);
        }

        return stats;
    }

    /**
     * 빈 문자열 / 공백 문자열이면 null 반환
     */
    private String normalize(String value) {
        return StringUtils.hasText(value) ? value : null;
    }

    /**
     * keyword 전용 정규화
     *
     * keyword가 없으면 "" 반환
     * → LIKE '%%' 가 되어 전체 검색처럼 동작
     */
    private String normalizeKeyword(String keyword) {
        return StringUtils.hasText(keyword) ? keyword : "";
    }

    /**
     * 시작일 변환
     */
    private LocalDateTime toStartDateTime(LocalDate startDate) {
        return startDate != null
                ? startDate.atStartOfDay()
                : LocalDateTime.of(1970, 1, 1, 0, 0, 0);
    }

    /**
     * 종료일 변환
     */
    private LocalDateTime toEndDateTime(LocalDate endDate) {
        return endDate != null
                ? endDate.atTime(LocalTime.MAX)
                : LocalDateTime.of(9999, 12, 31, 23, 59, 59);
    }

    /**
     * 로그 여러 건 저장
     *
     * Kafka batch consumer가 호출한다.
     */
    public void saveAll(List<LogMessageRequest> requests) {

        List<LogEvent> events = requests.stream()
                .map(request -> {
                    ErrorType errorType = ErrorClassifier.classify(request.getMessage());

                    String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

                    logStreamController.publishLog(
                            Map.of(
                                    "time", time,
                                    "service", request.getServiceName(),
                                    "level", request.getLevel(),
                                    "message", request.getMessage()
                            )
                    );

                    return LogEvent.builder()
                            .level(request.getLevel())
                            .serviceName(request.getServiceName())
                            .message(request.getMessage())
                            .errorType(errorType.name())
                            .createdAt(LocalDateTime.now())
                            .build();
                })
                .collect(Collectors.toList());

        repository.saveAll(events);
    }

    /**
     * retentionDays 이전 로그 삭제
     *
     * 예:
     * retentionDays = 7
     * → 7일보다 오래된 로그 삭제
     */
    public int deleteOldLogs(int retentionDays) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(retentionDays);

        int deletedCount = repository.deleteOldLogs(threshold);

        log.info("오래된 로그 삭제 완료. retentionDays={}, deletedCount={}, threshold={}",
                retentionDays, deletedCount, threshold);

        return deletedCount;
    }

    /**
     * 서비스 + 오류유형별 통계
     */
    public List<Map<String, Object>> getErrorStats() {

        List<Object[]> rows = repository.countErrorsByServiceAndType();

        return rows.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();

                    map.put("serviceName", row[0]);
                    map.put("errorType", row[1]);
                    map.put("count", row[2]);

                    return map;
                })
                .toList();
    }
}
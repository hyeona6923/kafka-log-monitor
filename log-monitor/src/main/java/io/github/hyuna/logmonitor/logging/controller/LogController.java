package io.github.hyuna.logmonitor.logging.controller;

import io.github.hyuna.logmonitor.kafka.producer.LogKafkaProducer;
import io.github.hyuna.logmonitor.logging.dto.LogMessageRequest;
import io.github.hyuna.logmonitor.logging.dto.LogResponse;
import io.github.hyuna.logmonitor.logging.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 로그 관련 API Controller
 *
 * 역할:
 * - 로그 생성
 * - 로그 조회
 * - 로그 통계 조회
 */
@Tag(name = "Log API", description = "로그 생성 / 조회 / 통계 API")
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogKafkaProducer logKafkaProducer;
    private final LogService logService;

    /**
     * 로그 생성 API
     */
    @Operation(summary = "로그 생성", description = "Kafka로 로그 메시지를 발행한다.")
    @PostMapping
    public ResponseEntity<String> sendLog(@Valid @RequestBody LogMessageRequest request) {
        logKafkaProducer.send(request);
        return ResponseEntity.ok("로그 메시지 전송 완료");
    }

    /**
 * 로그 목록 조회 API
 *
 * 조건 검색 가능:
 * - level
 * - serviceName
 * - keyword
 * - startDate
 * - endDate
 */
@Operation(summary = "로그 조회", description = "조건 기반 로그 목록 조회")
@GetMapping
public ResponseEntity<List<LogResponse>> getLogs(

        @Parameter(description = "로그 레벨", example = "ERROR")
        @RequestParam(required = false)
        String level,

        @Parameter(description = "서비스 이름", example = "payment")
        @RequestParam(required = false)
        String serviceName,

        @Parameter(description = "메시지 키워드", example = "결제")
        @RequestParam(required = false)
        String keyword,

        @Parameter(description = "조회 시작일", example = "2026-03-01")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate startDate,

        @Parameter(description = "조회 종료일", example = "2026-03-09")
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate endDate
) {

    return ResponseEntity.ok(
            logService.getLogs(level, serviceName, keyword, startDate, endDate)
    );
}

    /**
     * 로그 페이징 조회 API
     *
     * 예:
     * /api/logs/page?page=0&size=5
     * /api/logs/page?page=0&size=5&level=ERROR
     * /api/logs/page?page=0&size=5&keyword=결제
     * /api/logs/page?page=0&size=5&startDate=2026-03-01&endDate=2026-03-09
     */
    @GetMapping("/page")
    public ResponseEntity<Page<LogResponse>> getLogsWithPage(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String serviceName,
            @RequestParam(required = false) String keyword,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(
                logService.getLogsWithPage(level, serviceName, keyword, startDate, endDate, pageable)
        );
    }

    /**
     * 로그 레벨별 통계 조회 API
     */
    @GetMapping("/stats/level")
    public ResponseEntity<Map<String, Long>> getLogStatsByLevel() {
        return ResponseEntity.ok(
                logService.getLogStatsByLevel()
        );
    }

    /**
     * 서비스명별 로그 통계 조회 API
     */
    @GetMapping("/stats/service")
    public ResponseEntity<Map<String, Long>> getLogStatsByServiceName() {
        return ResponseEntity.ok(
                logService.getLogStatsByServiceName()
        );
    }

    @GetMapping("/stats/errors")
    public List<Map<String, Object>> getErrorStats() {
        return logService.getErrorStats();
    }
    
}
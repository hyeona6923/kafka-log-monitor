package io.github.hyuna.logmonitor.logging.scheduler;

import io.github.hyuna.logmonitor.logging.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 로그 보관 정책 스케줄러
 *
 * 역할:
 * - 일정 주기로 오래된 로그 삭제
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LogRetentionScheduler {

    private final LogService logService;

    /**
     * 매일 새벽 3시에 7일 초과 로그 삭제
     *
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteOldLogs() {
        log.info("오래된 로그 삭제 스케줄 시작");

        int deletedCount = logService.deleteOldLogs(7);

        log.info("오래된 로그 삭제 스케줄 종료. deletedCount={}", deletedCount);
    }

    @Scheduled(fixedDelay = 60000)
    public void deleteOldLogsTest() {
        log.info("테스트용 오래된 로그 삭제 스케줄 시작");

        int deletedCount = logService.deleteOldLogs(7);

        log.info("테스트용 오래된 로그 삭제 스케줄 종료. deletedCount={}", deletedCount);
    }
}
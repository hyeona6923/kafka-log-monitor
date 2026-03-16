package io.github.hyuna.logmonitor.logging.controller;

import io.github.hyuna.logmonitor.kafka.producer.LogKafkaProducer;
import io.github.hyuna.logmonitor.logging.dto.LogMessageRequest;
import io.github.hyuna.logmonitor.logging.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * DLQ 테스트용 Controller
 *
 * 역할:
 * - DLQ로 보내질 테스트 메시지를 Kafka에 발행
 *
 * 주의:
 * - local 환경에서만 활성화
 * - 운영에서는 노출되지 않도록 @Profile("local") 적용
 */
@Profile("local")
@RestController
@RequestMapping("/api/logs/test")
@RequiredArgsConstructor
public class LogDlqTestController {

    private final LogKafkaProducer logKafkaProducer;
    private final LogService logService;

    /**
     * DLQ 테스트용 메시지 발행
     *
     * 사용 예:
     * POST /api/logs/test/dlq
     *
     * 이 API는 serviceName을 "dlq-test"로 고정해서 보내고,
     * Consumer에서 이 값을 만나면 강제로 예외를 발생시키도록 테스트한다.
     */
    @PostMapping("/dlq")
    public ResponseEntity<String> sendDlqTestMessage() {

        LogMessageRequest request = LogMessageRequest.builder()
                .serviceName("dlq-test")
                .level("ERROR")
                .message("DLQ 테스트 메시지")
                .build();

        logKafkaProducer.send(request);

        return ResponseEntity.ok("DLQ 테스트 메시지 발행 완료");
    }
    
    @PostMapping("/delete-old")
    public ResponseEntity<String> deleteOldLogs() {
        int deletedCount = logService.deleteOldLogs(7);
        return ResponseEntity.ok("삭제 건수: " + deletedCount);
    }
}
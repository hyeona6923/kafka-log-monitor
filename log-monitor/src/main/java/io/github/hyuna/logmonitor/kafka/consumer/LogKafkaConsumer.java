package io.github.hyuna.logmonitor.kafka.consumer;

import io.github.hyuna.logmonitor.logging.controller.LogStreamController;
import io.github.hyuna.logmonitor.logging.dto.LogMessageRequest;
import io.github.hyuna.logmonitor.logging.enums.ErrorType;
import io.github.hyuna.logmonitor.logging.service.LogService;
import io.github.hyuna.logmonitor.logging.util.ErrorClassifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Kafka Consumer
 *
 * 역할:
 * - Kafka Topic에서 로그 메시지를 수신
 * - 여러 건을 한 번에 받아 DB에 batch 저장
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogKafkaConsumer {

    /**
     * 로그 비즈니스 로직 처리 서비스
     */
    private final LogService logService;


    /**
     * Kafka Batch Listener
     *
     * 이전에는 메시지 1건씩 받았지만
     * 이제는 List 형태로 여러 건을 한 번에 받는다.
     *
     * 장점:
     * - Kafka polling 횟수 감소
     * - DB insert 횟수 감소
     * - 대용량 처리에 더 적합
     */
    @KafkaListener(
            topics = "log-events",
            groupId = "log-monitor-group",
            containerFactory = "batchKafkaListenerContainerFactory"
    )
    public void consume(List<LogMessageRequest> requests) {

        /**
         * 수신 건수 로그 출력
         */
        log.info("Kafka batch 메시지 수신. size={}", requests.size());

        /**
         * 수신된 각 메시지를 간단히 로그로 출력
         */
        for (LogMessageRequest request : requests) {
            log.info("serviceName={}, level={}, message={}",
                    request.getServiceName(),
                    request.getLevel(),
                    request.getMessage());

            if ("dlq-test".equals(request.getServiceName())) {
                throw new RuntimeException("DLQ 테스트용 예외");
            }

        }


        /**
         * 여러 건을 한 번에 DB 저장
         */
        logService.saveAll(requests);

    }
}
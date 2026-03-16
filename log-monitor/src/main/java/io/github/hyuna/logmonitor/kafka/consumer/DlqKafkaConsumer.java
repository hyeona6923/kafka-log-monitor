package io.github.hyuna.logmonitor.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.hyuna.logmonitor.logging.dto.LogMessageRequest;
import io.github.hyuna.logmonitor.logging.entity.LogEventDlq;
import io.github.hyuna.logmonitor.persistence.logging.repository.LogEventDlqRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * DLQ 메시지 소비 Consumer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DlqKafkaConsumer {

    private final LogEventDlqRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "log-events-dlq",
            groupId = "log-monitor-dlq-group",
            containerFactory = "dlqKafkaListenerContainerFactory"
    )
    public void consume(LogMessageRequest message) throws JsonProcessingException {

        log.error("DLQ 메시지 수신 >>> serviceName={}, level={}, message={}",
                message.getServiceName(),
                message.getLevel(),
                message.getMessage());

        String payload = objectMapper.writeValueAsString(message);

        LogEventDlq entity = LogEventDlq.builder()
                .payload(payload)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(entity);
    }
}
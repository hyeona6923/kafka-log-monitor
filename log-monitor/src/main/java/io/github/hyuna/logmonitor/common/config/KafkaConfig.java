package io.github.hyuna.logmonitor.common.config;

import io.github.hyuna.logmonitor.logging.dto.LogMessageRequest;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka 공통 설정 클래스
 *
 * 역할:
 * - Topic 생성
 * - Producer 설정
 * - KafkaTemplate 생성
 * - Batch Consumer Listener Factory 생성
 * - Kafka Consumer ErrorHandler / DLQ 설정
 */
@Configuration
public class KafkaConfig {

    /**
     * 로그 이벤트용 Kafka Topic 생성
     */
    @Bean
    public NewTopic logEventsTopic() {
        return TopicBuilder.name("log-events")
                .partitions(3)   // 대용량 처리를 고려해 파티션 수 증가
                .replicas(1)
                .build();
    }

    /**
     * DLQ(Dead Letter Queue) Topic 생성
     *
     * 메시지 처리 실패 시 최종적으로 이동할 Topic
     */
    @Bean
    public NewTopic logEventsDlqTopic() {
        return TopicBuilder.name("log-events-dlq")
                .partitions(3)
                .replicas(1)
                .build();
    }

    /**
     * Kafka Producer 설정
     *
     * LogMessageRequest DTO를 JSON 형태로 Kafka에 보낸다.
     */
    @Bean
    public ProducerFactory<String, LogMessageRequest> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Kafka 메시지 발행용 템플릿
     */
    @Bean
    public KafkaTemplate<String, LogMessageRequest> kafkaTemplate(
            ProducerFactory<String, LogMessageRequest> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * Kafka Consumer 공통 ErrorHandler
     *
     * 동작:
     * 1. 메시지 처리 실패
     * 2. 1초 간격으로 최대 3번 재시도
     * 3. 그래도 실패하면 DLQ Topic(log-events-dlq)으로 전송
     *
     * 주의:
     * - DeadLetterPublishingRecoverer는 KafkaTemplate이 필요하다.
     * - 메인 토픽과 동일한 partition 번호를 유지한 채 DLQ로 보낸다.
     */
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, LogMessageRequest> kafkaTemplate
    ) {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(
                        kafkaTemplate,
                        (record, ex) -> new TopicPartition(
                                record.topic() + "-dlq",
                                record.partition()
                        )
                );

        return new DefaultErrorHandler(
                recoverer,
                new FixedBackOff(1000L, 3) // 1초 간격으로 3번 재시도
        );
    }

    /**
     * Batch Kafka Listener Factory
     *
     * Consumer가 Kafka 메시지를 1건씩 받지 않고
     * 여러 건을 List 형태로 받도록 설정한다.
     *
     * 추가:
     * - 공통 ErrorHandler 연결
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> batchKafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            org.springframework.kafka.core.ConsumerFactory<Object, Object> consumerFactory,
            DefaultErrorHandler kafkaErrorHandler
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        configurer.configure(factory, consumerFactory);

        // Batch Listener 활성화
        factory.setBatchListener(true);

        // 공통 ErrorHandler 연결
        factory.setCommonErrorHandler(kafkaErrorHandler);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> dlqKafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            org.springframework.kafka.core.ConsumerFactory<Object, Object> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        configurer.configure(factory, consumerFactory);

        // DLQ는 단건 처리
        factory.setBatchListener(false);

        return factory;
    }
}
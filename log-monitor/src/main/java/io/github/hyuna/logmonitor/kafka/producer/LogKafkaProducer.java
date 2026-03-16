package io.github.hyuna.logmonitor.kafka.producer;

import io.github.hyuna.logmonitor.logging.dto.LogMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Kafka Producer 역할
 *
 * API로 들어온 로그 데이터를 Kafka Topic으로 발행한다.
 *
 * 흐름:
 * Controller → Producer → Kafka Topic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LogKafkaProducer {

    /**
     * Kafka Topic 이름
     * 로그 이벤트를 전달하는 Topic
     */
    private static final String TOPIC_NAME = "log-events";

    /**
     * bucket 개수
     *
     * 목적:
     * - 특정 서비스 TPS가 높을 때
     *   serviceName 단독 key 사용 시 한 partition으로 몰리는 현상을 완화
     *
     * 예:
     * payment-0
     * payment-1
     * payment-2
     * payment-3
     *
     * 주의:
     * - partition 수보다 너무 크게 잡아도 큰 의미는 없다
     * - 현재 topic partition 수가 3개라면 3~4 정도가 무난하다
     */
    private static final int BUCKET_SIZE = 4;

    /**
     * Spring Kafka에서 제공하는 KafkaTemplate
     *
     * Kafka 메시지를 보내기 위한 핵심 객체
     */
    private final KafkaTemplate<String, LogMessageRequest> kafkaTemplate;

    /**
     * Kafka로 로그 메시지 전송
     *
     * 기존에는 serviceName만 key로 사용했지만,
     * 특정 서비스 TPS가 높아질 경우 partition hot spot이 발생할 수 있다.
     *
     * 그래서 serviceName + bucket 전략으로 key를 생성하여
     * 같은 서비스 로그도 여러 partition으로 분산될 수 있도록 한다.
     *
     * @param request 로그 메시지 DTO
     */
    public void send(LogMessageRequest request) {

        /**
         * Kafka partition 분산용 key 생성
         *
         * 예:
         * payment-0
         * payment-1
         * auth-2
         */
        String partitionKey = createPartitionKey(request);

        // Kafka Topic으로 메시지 발행
        kafkaTemplate.send(TOPIC_NAME, partitionKey, request);

        // 로그 출력 (서버 로그 확인용)
        log.info(
                "Kafka 메시지 발행 완료. topic={}, partitionKey={}, serviceName={}, level={}, message={}",
                TOPIC_NAME,
                partitionKey,
                request.getServiceName(),
                request.getLevel(),
                request.getMessage()
        );
    }

    /**
     * Kafka partition 분산용 key 생성
     *
     * 전략:
     * - serviceName + random bucket
     * - 특정 서비스 로그가 한 partition으로 몰리는 현상 완화
     *
     * 예:
     * payment-0
     * payment-1
     * payment-2
     */
    private String createPartitionKey(LogMessageRequest request) {

        // 0 ~ BUCKET_SIZE-1 범위의 랜덤 bucket 생성
        int bucket = ThreadLocalRandom.current().nextInt(BUCKET_SIZE);

        return request.getServiceName() + "-" + bucket;
    }
}
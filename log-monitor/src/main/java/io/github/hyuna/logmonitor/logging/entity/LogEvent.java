package io.github.hyuna.logmonitor.logging.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 로그 이벤트 Entity
 *
 * DB 테이블: log_event
 *
 * Kafka에서 들어온 로그 데이터를 저장한다.
 */
@Entity
@Table(name = "log_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEvent {

    /**
     * PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 로그 레벨
     * INFO / WARN / ERROR
     */
    private String level;

    /**
     * 로그 발생 서비스
     */
    private String serviceName;

    /**
     * 로그 메시지
     */
    @Column(length = 2000)
    private String message;


    @Column(name = "error_type")
    private String errorType;

    /**
     * 로그 생성 시간
     */
    private LocalDateTime createdAt;
}
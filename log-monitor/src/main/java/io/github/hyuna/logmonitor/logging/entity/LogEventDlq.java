package io.github.hyuna.logmonitor.logging.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DLQ 로그 엔티티
 *
 * 역할:
 * - Kafka 처리 실패 메시지를 DB에 저장
 */
@Entity
@Table(name = "log_event_dlq")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEventDlq {

    /**
     * PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 실패한 원본 메시지 payload
     */
    @Column(columnDefinition = "TEXT")
    private String payload;

    /**
     * DLQ 저장 시각
     */
    private LocalDateTime createdAt;

    /**
     * insert 직전 현재 시각 자동 세팅
     */
    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}
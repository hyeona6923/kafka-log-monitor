package io.github.hyuna.logmonitor.logging.dto;

import io.github.hyuna.logmonitor.logging.entity.LogEvent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LogResponse {

    private Long id;
    private String level;
    private String serviceName;
    private String message;
    private LocalDateTime createdAt;

    public static LogResponse from(LogEvent entity) {
        return LogResponse.builder()
                .id(entity.getId())
                .level(entity.getLevel())
                .serviceName(entity.getServiceName())
                .message(entity.getMessage())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
package io.github.hyuna.logmonitor.logging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 로그 생성 요청 DTO
 *
 * 로그를 Kafka로 전송하기 위한 요청 객체
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "로그 생성 요청")
public class LogMessageRequest {

    /**
     * 로그 레벨
     */
    @Schema(description = "로그 레벨", example = "ERROR")
    private String level;

    /**
     * 로그 발생 서비스
     */
    @Schema(description = "서비스 이름", example = "payment")
    private String serviceName;

    /**
     * 로그 메시지
     */
    @Schema(description = "로그 메시지", example = "결제 오류 발생")
    private String message;
}
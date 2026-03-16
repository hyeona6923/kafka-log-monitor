package io.github.hyuna.logmonitor.logging.controller;

import io.github.hyuna.logmonitor.kafka.producer.LogKafkaProducer;
import io.github.hyuna.logmonitor.logging.dto.LogMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * 로컬 테스트용 로그 생성 Controller
 */
@Profile("local")
@RestController
@RequestMapping("/api/logs/test")
@RequiredArgsConstructor
public class LogTestController {

    private final LogKafkaProducer logKafkaProducer;

    @PostMapping("/generate/errors")
    public ResponseEntity<String> generateErrorLogs(
            @RequestParam(defaultValue = "1000") int count
    ) {
        List<String> services = List.of(
                "member",
                "payment",
                "order",
                "inventory",
                "loan",
                "auth",
                "notification"
        );

        List<String> messages = List.of(
                "DB connection failed",
                "SQL query execution error",
                "validation error: invalid input",
                "validation failed: missing field",
                "external api timeout",
                "payment gateway timeout",
                "network connection refused",
                "network unreachable",
                "unexpected system error"
        );

        Random random = new Random();

        for (int i = 0; i < count; i++) {
            String service = services.get(random.nextInt(services.size()));
            String message = messages.get(random.nextInt(messages.size()));

            LogMessageRequest request = LogMessageRequest.builder()
                    .serviceName(service)
                    .level("ERROR")
                    .message(message)
                    .build();

            logKafkaProducer.send(request);
        }

        return ResponseEntity.ok(count + "건 테스트 ERROR 로그 생성 완료");
    }
}
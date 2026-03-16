package io.github.hyuna.logmonitor.logging.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 테스트용 서비스명 데이터 로더
 *
 * 역할:
 * - resources/test-data/services.json 파일을 읽어서
 *   서비스명 목록을 메모리에 로딩한다.
 *
 * 특징:
 * - local 프로필에서만 동작
 */
@Profile("local")
@Component
@Getter
public class TestServiceDataLoader {

    /**
     * JSON 파일에서 읽어온 서비스명 목록
     */
    private List<String> serviceNames = Collections.emptyList();

    /**
     * 애플리케이션 시작 시 JSON 파일 로딩
     */
    @PostConstruct
    public void load() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            InputStream inputStream =
                    new ClassPathResource("test-data/services.json").getInputStream();

            JsonNode root = objectMapper.readTree(inputStream);
            JsonNode servicesNode = root.get("services");

            if (servicesNode == null || !servicesNode.isArray()) {
                throw new IllegalArgumentException("services.json에 services 배열이 없습니다.");
            }

            List<String> loadedServiceNames = new ArrayList<>();

            for (JsonNode node : servicesNode) {
                loadedServiceNames.add(node.asText());
            }

            if (loadedServiceNames.isEmpty()) {
                throw new IllegalArgumentException("services.json의 services 배열이 비어 있습니다.");
            }

            this.serviceNames = loadedServiceNames;

            System.out.println("서비스 목록 로딩 완료: " + serviceNames);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("services.json 로딩 실패", e);
        }
    }
}
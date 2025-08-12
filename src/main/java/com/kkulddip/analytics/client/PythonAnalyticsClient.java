package com.kkulddip.analytics.client;

import com.kkulddip.analytics.dto.request.AnalyticsRequestDto;
import com.kkulddip.analytics.dto.response.AnalyticsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PythonAnalyticsClient {

    private final RestTemplate restTemplate;

    @Value("${python.analytics.base-url:http://localhost:8001}")
    private String pythonBaseUrl;

    public AnalyticsResponseDto analyzeData(AnalyticsRequestDto request) {
        try {
            String url = pythonBaseUrl + "/api/analytics/analyze";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<AnalyticsRequestDto> entity = new HttpEntity<>(request, headers);

            log.info("Calling Python API: {}", url);
            ResponseEntity<AnalyticsResponseDto> response = restTemplate.postForEntity(url, entity, AnalyticsResponseDto.class);

            log.info("Python API call successful");
            return response.getBody();

        } catch (Exception e) {
            log.error("Python API call failed: {}", e.getMessage());
            throw new RuntimeException("Analytics processing failed", e);
        }
    }
}

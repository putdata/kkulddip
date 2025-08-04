package com.kkulddip.payment.infrastructure.external.toss;

import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentConfirmRequest;
import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TossPaymentsApiClient {

    private final WebClient webClient;

    @Value("${toss.payments.test-secret-key}")
    private String secretKey;

    @Value("${toss.payments.base-url}")
    private String baseUrl;

    public TossPaymentResponse confirmPayment(String paymentKey, String orderId, long amount) {
        String encodedAuth = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());

        TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(paymentKey, orderId, amount);

        WebClient client = WebClient.builder()
                .baseUrl(baseUrl)
                .build();

        try {
            return client.post()
                    .uri("/v1/payments/confirm")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            status -> status.isError(), // HttpStatus::isError 대신 lambda 사용
                            response -> response.bodyToMono(String.class)
                                    .map(errorBody -> { // Mono.error 대신 map 사용
                                        log.error("토스페이먼츠 API 오류: {}", errorBody);
                                        return new RuntimeException("결제 승인 API 호출 실패: " + errorBody);
                                    })
                    )
                    .bodyToMono(TossPaymentResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("토스페이먼츠 API 오류: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("결제 승인 API 호출 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("예상치 못한 오류: {}", e.getMessage());
            throw new RuntimeException("결제 승인 API 호출 실패: " + e.getMessage());
        }
    }

    public TossPaymentResponse cancelPayment(String paymentKey, String cancelReason) {
        String encodedAuth = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());

        Map<String, String> request = Map.of("cancelReason", cancelReason);

        WebClient client = WebClient.builder()
                .baseUrl(baseUrl)
                .build();

        try {
            return client.post()
                    .uri("/v1/payments/{paymentKey}/cancel", paymentKey)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            response -> response.bodyToMono(String.class)
                                    .map(errorBody -> {
                                        log.error("토스페이먼츠 취소 API 오류: {}", errorBody);
                                        return new RuntimeException("결제 취소 API 호출 실패: " + errorBody);
                                    })
                    )
                    .bodyToMono(TossPaymentResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("결제 취소 오류: {}", e.getMessage());
            throw new RuntimeException("결제 취소 API 호출 실패: " + e.getMessage());
        }
    }

    public TossPaymentResponse getPayment(String paymentKey) {
        String encodedAuth = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());

        WebClient client = WebClient.builder()
                .baseUrl(baseUrl)
                .build();

        try {
            return client.get()
                    .uri("/v1/payments/{paymentKey}", paymentKey)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth)
                    .retrieve()
                    .bodyToMono(TossPaymentResponse.class)
                    .block();
        } catch (Exception e) {
            log.error("결제 조회 오류: {}", e.getMessage());
            throw new RuntimeException("결제 조회 API 호출 실패: " + e.getMessage());
        }
    }
}
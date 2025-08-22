package com.kkulddip.payment.infrastructure.external.toss;

import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentConfirmRequest;
import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class TossPaymentsApiClient {

    private static final String PAYMENTS_CONFIRM_PATH = "/v1/payments/confirm";
    private static final String PAYMENTS_CANCEL_PATH = "/v1/payments/{paymentKey}/cancel";
    private static final String PAYMENTS_GET_PATH = "/v1/payments/{paymentKey}";
    private static final String AUTHORIZATION_PREFIX = "Basic ";
    private static final String AUTH_SEPARATOR = ":";

    private final WebClient webClient;

    @Value("${toss.payments.test-secret-key}")
    private String secretKey;

    @Value("${toss.payments.base-url}")
    private String baseUrl;

    public TossPaymentResponse confirmPayment(String paymentKey, String orderId, long amount) {
        TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(paymentKey, orderId, amount);

        try {
            return webClient.post()
                .uri(baseUrl + PAYMENTS_CONFIRM_PATH)
                .header(HttpHeaders.AUTHORIZATION, createAuthHeader())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                    status -> status.isError(),
                    response -> handleApiError(response, "결제 승인 API 호출 실패")
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
        Map<String, String> request = Map.of("cancelReason", cancelReason);

        try {
            return webClient.post()
                .uri(baseUrl + PAYMENTS_CANCEL_PATH, paymentKey)
                .header(HttpHeaders.AUTHORIZATION, createAuthHeader())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .onStatus(
                    status -> status.isError(),
                    response -> handleApiError(response, "결제 취소 API 호출 실패")
                )
                .bodyToMono(TossPaymentResponse.class)
                .block();
        } catch (WebClientResponseException e) {
            log.error("토스페이먼츠 취소 API 오류: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("결제 취소 API 호출 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("결제 취소 오류: {}", e.getMessage());
            throw new RuntimeException("결제 취소 API 호출 실패: " + e.getMessage());
        }
    }

    public TossPaymentResponse getPayment(String paymentKey) {
        try {
            return webClient.get()
                .uri(baseUrl + PAYMENTS_GET_PATH, paymentKey)
                .header(HttpHeaders.AUTHORIZATION, createAuthHeader())
                .retrieve()
                .onStatus(
                    status -> status.isError(),
                    response -> handleApiError(response, "결제 조회 API 호출 실패")
                )
                .bodyToMono(TossPaymentResponse.class)
                .block();
        } catch (WebClientResponseException e) {
            log.error("토스페이먼츠 조회 API 오류: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("결제 조회 API 호출 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("결제 조회 오류: {}", e.getMessage());
            throw new RuntimeException("결제 조회 API 호출 실패: " + e.getMessage());
        }
    }

    private String createAuthHeader() {
        String encodedAuth = Base64.getEncoder().encodeToString((secretKey + AUTH_SEPARATOR).getBytes());
        return AUTHORIZATION_PREFIX + encodedAuth;
    }

    private Mono<? extends Throwable> handleApiError(ClientResponse response, String errorMessage) {
        return response.bodyToMono(String.class)
            .map(errorBody -> {
                log.error("토스페이먼츠 API 오류: {}", errorBody);
                return new RuntimeException(errorMessage + ": " + errorBody);
            });
    }
}
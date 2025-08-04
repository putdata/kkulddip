package com.kkulddip.payment.interfaces.scheduled;

import com.kkulddip.payment.application.mapper.PaymentMapper;
import com.kkulddip.payment.domain.model.entity.Payment;
import com.kkulddip.payment.domain.repository.PaymentRepository;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import com.kkulddip.payment.domain.model.status.PaymentMethod;
import com.kkulddip.payment.infrastructure.external.toss.TossPaymentsApiClient;
import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentResponse;
import com.kkulddip.payment.infrastructure.persistence.redis.RedisOrderRequestService;
import com.kkulddip.payment.interfaces.dto.request.OrderRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentScheduleService {

    private final RedisOrderRequestService redisOrderRequestService;
    private final TossPaymentsApiClient tossPaymentsApiClient;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RestTemplate restTemplate;

    @Scheduled(fixedDelay = 5000) // 5초마다 실행
    @Transactional
    public void processPaymentRequests() {
        log.debug("결제 요청 처리 스케줄러 시작");
        
        try {
            Set<OrderRequestDto> orderRequests = redisOrderRequestService.getOrderRequests(10);
            
            if (orderRequests.isEmpty()) {
                log.debug("처리할 결제 요청이 없습니다");
                return;
            }

            log.info("처리할 결제 요청 수: {}", orderRequests.size());

            for (OrderRequestDto orderRequest : orderRequests) {
                processPaymentRequest(orderRequest);
            }
            
        } catch (Exception e) {
            log.error("결제 요청 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    private void processPaymentRequest(OrderRequestDto orderRequest) {
        try {
            log.info("결제 요청 처리 시작: orderId={}", orderRequest.orderId());

            // Payment 엔티티 생성
            Payment payment = paymentMapper.toEntity(orderRequest);

            // 결제 정보 저장
            Payment savedPayment = paymentRepository.save(payment);
            log.info("결제 정보 저장 완료: orderId={}", orderRequest.orderId());

            // Toss 결제 생성
            TossPaymentResponse tossResponse = createTossPayment(orderRequest);
            
            if (tossResponse != null) {
                // 결제 승인 처리
                savedPayment.approve(
                        PaymentKey.of(tossResponse.paymentKey()),
                        PaymentMethod.fromString(tossResponse.method()),
                        tossResponse.requestedAt(),
                        tossResponse.approvedAt(),
                        tossResponse.getReceiptUrl()
                );

                paymentRepository.save(savedPayment);
                
                // 성공 콜백 호출
                sendCallback(orderRequest.callbackUrl(), savedPayment, true);
                log.info("결제 처리 성공: orderId={}", orderRequest.orderId());
            } else {
                // 결제 실패 처리
                savedPayment.fail();
                paymentRepository.save(savedPayment);
                
                // 실패 콜백 호출
                sendCallback(orderRequest.failUrl(), savedPayment, false);
                log.warn("결제 처리 실패: orderId={}", orderRequest.orderId());
            }

            // Redis에서 처리완료된 요청 제거
            redisOrderRequestService.removeOrderRequest(orderRequest.orderId());

        } catch (Exception e) {
            log.error("결제 요청 처리 실패: orderId={}, error={}", orderRequest.orderId(), e.getMessage(), e);
            
            // 실패 콜백 호출
            sendCallback(orderRequest.failUrl(), null, false);
        }
    }

    private TossPaymentResponse createTossPayment(OrderRequestDto orderRequest) {
        try {
            String tempPaymentKey = "temp_" + orderRequest.orderId();
            
            return tossPaymentsApiClient.confirmPayment(
                    tempPaymentKey,
                    orderRequest.orderId(),
                    orderRequest.amount()
            );
            
        } catch (Exception e) {
            log.error("Toss 결제 생성 실패: orderId={}, error={}", orderRequest.orderId(), e.getMessage());
            return null;
        }
    }

    private void sendCallback(String callbackUrl, Payment payment, boolean success) {
        if (callbackUrl == null || callbackUrl.trim().isEmpty()) {
            log.debug("콜백 URL이 없습니다");
            return;
        }

        try {
            Map<String, Object> callbackData = new HashMap<>();
            callbackData.put("success", success);
            
            if (payment != null) {
                callbackData.put("orderId", payment.getOrderId());
                callbackData.put("amount", payment.getAmount().value());
                callbackData.put("status", payment.getStatus().name());
                if (payment.getPaymentKey() != null) {
                    callbackData.put("paymentKey", payment.getPaymentKey().value());
                }
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(callbackData, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(callbackUrl, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("콜백 전송 성공: url={}, orderId={}", callbackUrl, 
                        payment != null ? payment.getOrderId() : "unknown");
            } else {
                log.warn("콜백 전송 실패: url={}, status={}", callbackUrl, response.getStatusCode());
            }
            
        } catch (Exception e) {
            log.error("콜백 전송 오류: url={}, error={}", callbackUrl, e.getMessage());
        }
    }
}
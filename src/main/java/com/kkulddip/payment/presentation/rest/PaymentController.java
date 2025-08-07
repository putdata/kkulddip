package com.kkulddip.payment.presentation.rest;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.payment.application.facade.PaymentFacade;
import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentConfirmRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import com.kkulddip.payment.infrastructure.external.toss.utils.TossOrderIdConverter;
import com.kkulddip.payment.presentation.dto.response.PaymentResponse;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/payments")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    /**
     * 2. Client로부터 [paymentKey, orderId] 수신하여 결제 확정 처리
     */
    @PostMapping("/confirm")
    public ApiResponse<PaymentResponse> confirmPayment(
        @Valid @RequestBody TossPaymentConfirmRequest request) {

        log.info("📝 결제 확정 요청: paymentKey={}, orderId={}",
            request.paymentKey(), request.orderId());

        try {
            PaymentResponse response = paymentFacade.confirmPayment(request);

            log.info("✅ 결제 확정 완료: orderId={}, status={}",
                response.orderId(), response.status());

            return ApiResponse.of(response);

        } catch (Exception e) {
            log.error("❌ 결제 확정 실패: orderId={}, error={}",
                request.orderId(), e.getMessage(), e);
            return ApiResponse.of(500, null);
        }
    }

    @GetMapping("/{paymentKey}")
    public ApiResponse<PaymentResponse> getPayment(@PathVariable String paymentKey) {
        try {
            PaymentResponse response = paymentFacade.getPayment(paymentKey);
            return ApiResponse.of(response);
        } catch (Exception e) {
            log.error("결제 조회 실패: paymentKey={}, error={}", paymentKey, e.getMessage());
            return ApiResponse.of(400, null);
        }
    }

    @GetMapping("/orders/{orderId}")
    public ApiResponse<PaymentResponse> getPaymentByOrderId(@PathVariable String orderId) {
        try {
            // 토스페이먼츠 형식의 orderId인 경우 변환하여 조회
            String queryOrderId = orderId;
            if (TossOrderIdConverter.isTossOrderIdFormat(orderId)) {
                Long longOrderId = TossOrderIdConverter.fromTossOrderId(orderId);
                queryOrderId = longOrderId.toString();
            }
            
            PaymentResponse response = paymentFacade.getPaymentByOrderId(queryOrderId);
            return ApiResponse.of(response);
        } catch (Exception e) {
            log.error("주문별 결제 조회 실패: orderId={}, error={}", orderId, e.getMessage());
            return ApiResponse.of(400, null);
        }
    }

    @PostMapping("/{paymentKey}/cancel")
    public ApiResponse<PaymentResponse> cancelPayment(
            @PathVariable String paymentKey,
            @RequestParam String cancelReason) {
        try {
            PaymentResponse response = paymentFacade.cancelPayment(paymentKey, cancelReason);
            return ApiResponse.of(response);
        } catch (Exception e) {
            log.error("결제 취소 실패: paymentKey={}, error={}", paymentKey, e.getMessage());
            return ApiResponse.of(400, null);
        }
    }
}
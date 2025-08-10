package com.kkulddip.payment.presentation.rest;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.lock.DistributedLock;
import com.kkulddip.payment.application.exception.PaymentException;
import com.kkulddip.payment.application.facade.PaymentFacade;
import com.kkulddip.payment.infrastructure.external.toss.dto.TossPaymentConfirmRequest;
import com.kkulddip.payment.presentation.dto.request.RequestPaymentRequest;
import com.kkulddip.payment.presentation.dto.response.PaymentResponse;
import com.kkulddip.payment.presentation.dto.response.PaymentOrderIdResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/payments")
public class PaymentController {

    private final PaymentFacade paymentFacade;
    private final DistributedLock distributedLock;

    /**
     * 2. Client로부터 [paymentKey, orderId] 수신하여 결제 확정 처리
     */
    @PostMapping("/confirm")
    public ApiResponse<PaymentResponse> confirmPayment(
        @Valid @RequestBody TossPaymentConfirmRequest request) {

        log.info("📝 결제 확정 요청: paymentKey={}, paymentOrderId={}",
            request.paymentKey(), request.orderId());

        try {
            PaymentResponse response = paymentFacade.confirmPayment(request);

            log.info("✅ 결제 확정 완료: paymentOrderId={}, status={}",
                response.paymentOrderId(), response.status());

            return ApiResponse.of(response);

        } catch (Exception e) {
            log.error("❌ 결제 확정 실패: paymentOrderId={}, error={}",
                request.orderId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * PaymentOrderId 발급 - TTL 검증 후 유효한 PaymentOrderId 반환
     */
    @PostMapping("/payment-order-id")
    public ApiResponse<PaymentOrderIdResponse> getPaymentOrderId(@RequestBody RequestPaymentRequest request) {
        try {
            String paymentOrderId = paymentFacade.requestPayment(request.orderId());
            log.info("PaymentOrderId 발급 성공: orderId={}, paymentOrderId={}", request.orderId(), paymentOrderId);
            
            PaymentOrderIdResponse response = new PaymentOrderIdResponse(paymentOrderId);
            return ApiResponse.of(response);
        } catch (Exception e) {
            log.error("PaymentOrderId 발급 실패: orderId={}, error={}", request.orderId(), e.getMessage());
            throw e;
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
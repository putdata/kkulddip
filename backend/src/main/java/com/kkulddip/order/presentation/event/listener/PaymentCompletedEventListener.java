package com.kkulddip.order.presentation.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kkulddip.order.application.dto.request.HandlePaymentResultRequest;
import com.kkulddip.order.application.facade.OrderProcessFacade;
import com.kkulddip.common.event.PaymentResultEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentCompletedEventListener {

    private final OrderProcessFacade orderProcessFacade;

    /**
     * 결제 완료 이벤트 수신
     * - Event를 Request로 매핑하여 Application 계층으로 위임
     */
    @Async("eventTaskExecutor")
    @EventListener
    public void handlePaymentCompleted(PaymentResultEvent event) {
        log.debug("Presentation 계층에서 결제 완료 이벤트 수신 - orderId: {}", event.orderId());
        
        try {
            // Event를 Request로 매핑
            HandlePaymentResultRequest request = HandlePaymentResultRequest.builder()
                .orderId(event.orderId())
                .status(event.status())
                .build();
            
            // Application 계층으로 위임
            orderProcessFacade.handlePaymentResult(request);
            
        } catch (Exception e) {
            log.error("결제 완료 이벤트 처리 실패 - orderId: {}, error: {}", 
                event.orderId(), e.getMessage(), e);
            // TODO: 실패 처리 로직 구현 (재시도, 데드레터 큐 등)
        }
    }
}
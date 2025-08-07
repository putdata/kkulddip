package com.kkulddip.order.presentation.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kkulddip.order.application.dto.request.HandleOrderConfirmedRequest;
import com.kkulddip.order.application.facade.OrderFacade;
import com.kkulddip.common.event.OrderConfirmedEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class OrderConfirmedEventListener {

    private final OrderFacade orderFacade;

    /**
     * 주문 확정 이벤트 수신
     * - Event를 Request로 매핑하여 Application 계층으로 위임
     */
    @Async("eventTaskExecutor")
    @EventListener
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        log.debug("Presentation 계층에서 주문 확정 이벤트 수신 - orderId: {}", event.orderId());
        
        try {
            // Event를 Request로 매핑
            HandleOrderConfirmedRequest request = HandleOrderConfirmedRequest.builder()
                .orderId(event.orderId())
                .storeId(event.storeId())
                .build();
            
            // Application 계층으로 위임
            orderFacade.handleOrderConfirmed(request);
            
        } catch (Exception e) {
            log.error("주문 확정 이벤트 처리 실패 - orderId: {}, error: {}", 
                event.orderId(), e.getMessage(), e);
            // TODO: 실패 처리 로직 구현 (재시도, 데드레터 큐 등)
        }
    }
}
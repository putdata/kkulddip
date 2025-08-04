//package com.kkulddip.payment.interfaces.event;
//
//import com.kkulddip.payment.domain.event.OrderCreatedEvent;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class OrderService {
//
//    private final OrderRepository orderRepository;
//    private final ApplicationEventPublisher eventPublisher;
//
//    @Transactional
//    public OrderResponse createOrder(OrderCreateRequest request) {
//
//        // 1. Order 엔티티 생성 및 저장
//        Order order = new Order(
//            generateOrderId(),
//            request.getOrderName(),
//            request.getAmount(),
//            request.getCustomerName(),
//            request.getCustomerEmail()
//        );
//
//        orderRepository.save(order);
//        log.info("✅ Order 저장: orderId={}, amount={}",
//            order.getOrderId(), order.getAmount());
//
//        // 2. 🔥 Payment 서버로 이벤트 발행: [orderId, amount]
//        OrderCreatedEvent event = new OrderCreatedEvent(
//            order.getOrderId(),
//            order.getAmount()
//        );
//
//        eventPublisher.publishEvent(event);
//        log.info("📤 주문 생성 이벤트 발행: orderId={}, amount={}",
//            event.orderId(), event.amount());
//
//        return new OrderResponse(order.getOrderId(), "PROCESSING");
//    }
//
//    @Transactional
//    public void markAsPaymentCompleted(String orderId) {
//        Order order = orderRepository.findByOrderId(orderId)
//            .orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다: " + orderId));
//
//        order.markAsPaymentCompleted();
//        orderRepository.save(order);
//    }
//
//    @Transactional
//    public void markAsPaymentFailed(String orderId) {
//        Order order = orderRepository.findByOrderId(orderId)
//            .orElseThrow(() -> new OrderNotFoundException("주문을 찾을 수 없습니다: " + orderId));
//
//        order.markAsPaymentFailed();
//        orderRepository.save(order);
//    }
//
//    private String generateOrderId() {
//        return "ORDER_" + System.currentTimeMillis();
//    }
//}
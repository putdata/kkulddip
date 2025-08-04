package com.kkulddip.payment.interfaces.event;

///**
// *  order 도메인 서버에서 사용할 이벤트 리스너 (참고용)
// */
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class OrderEventHandler {
//
//    private final OrderService orderService;
//
//    /**
//     * 3. Payment 서버로부터 [orderId, status] 수신하여 Order 상태 업데이트
//     */
//    @EventListener
//    @Transactional
//    public void handlePaymentResult(PaymentResultEvent event) {
//        try {
//            log.info("📥 결제 결과 이벤트 수신: orderId={}, status={}",
//                event.orderId(), event.status());
//
//            // status 값에 따라 Order 상태 업데이트
//            if ("DONE".equals(event.status())) {
//                orderService.markAsPaymentCompleted(event.orderId());
//                log.info("✅ 주문 결제 완료: orderId={}", event.orderId());
//
//            } else if ("ABORTED".equals(event.status())) {
//                orderService.markAsPaymentFailed(event.orderId());
//                log.info("❌ 주문 결제 실패: orderId={}", event.orderId());
//
//            } else {
//                log.warn("⚠️ 알 수 없는 결제 상태: orderId={}, status={}",
//                    event.orderId(), event.status());
//            }
//
//        } catch (Exception e) {
//            log.error("❌ Order 상태 업데이트 실패: orderId={}, error={}",
//                event.orderId(), e.getMessage(), e);
//            throw e;
//        }
//    }
//}
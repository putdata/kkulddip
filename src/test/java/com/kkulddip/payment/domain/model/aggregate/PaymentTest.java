package com.kkulddip.payment.domain.model.aggregate;

import com.kkulddip.payment.domain.model.status.PaymentMethod;
import com.kkulddip.payment.domain.model.status.PaymentStatus;
import com.kkulddip.payment.domain.model.vo.Money;
import com.kkulddip.payment.domain.model.vo.PaymentKey;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Payment 엔티티 테스트")
class PaymentTest {

    @Test
    @DisplayName("결제 생성 - 유효한 파라미터로 결제를 생성할 수 있다")
    void createPayment_ValidParameters_Success() {
        // given
        Long orderId = 123L;
        String orderName = "테스트 주문";
        Money amount = Money.of(10000);
        String customerName = "홍길동";
        String customerEmail = "test@example.com";
        String callbackUrl = "http://callback.url";
        String failUrl = "http://fail.url";

        // when
        Payment payment = new Payment(orderId, orderName, amount, customerName, customerEmail, callbackUrl, failUrl);

        // then
        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getOrderName()).isEqualTo(orderName);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getCustomerName()).isEqualTo(customerName);
        assertThat(payment.getCustomerEmail()).isEqualTo(customerEmail);
        assertThat(payment.getCallbackUrl()).isEqualTo(callbackUrl);
        assertThat(payment.getFailUrl()).isEqualTo(failUrl);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.READY);
        assertThat(payment.getCreatedAt()).isNotNull();
        assertThat(payment.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("결제 생성 - 주문명이 null이면 예외가 발생한다")
    void createPayment_NullOrderName_ThrowsException() {
        // given
        Long orderId = 123L;
        String orderName = null;
        Money amount = Money.of(10000);
        String customerName = "홍길동";
        String customerEmail = "test@example.com";
        String callbackUrl = "http://callback.url";
        String failUrl = "http://fail.url";

        // when & then
        assertThatThrownBy(() -> new Payment(orderId, orderName, amount, customerName, customerEmail, callbackUrl, failUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문명은 필수입니다.");
    }

    @Test
    @DisplayName("결제 생성 - 주문명이 빈 문자열이면 예외가 발생한다")
    void createPayment_EmptyOrderName_ThrowsException() {
        // given
        Long orderId = 123L;
        String orderName = "";
        Money amount = Money.of(10000);
        String customerName = "홍길동";
        String customerEmail = "test@example.com";
        String callbackUrl = "http://callback.url";
        String failUrl = "http://fail.url";

        // when & then
        assertThatThrownBy(() -> new Payment(orderId, orderName, amount, customerName, customerEmail, callbackUrl, failUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("주문명은 필수입니다.");
    }

    @Test
    @DisplayName("결제 생성 - 고객명이 null이면 예외가 발생한다")
    void createPayment_NullCustomerName_ThrowsException() {
        // given
        Long orderId = 123L;
        String orderName = "테스트 주문";
        Money amount = Money.of(10000);
        String customerName = null;
        String customerEmail = "test@example.com";
        String callbackUrl = "http://callback.url";
        String failUrl = "http://fail.url";

        // when & then
        assertThatThrownBy(() -> new Payment(orderId, orderName, amount, customerName, customerEmail, callbackUrl, failUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("고객명은 필수입니다.");
    }

    @Test
    @DisplayName("결제 승인 - READY 상태의 결제를 승인할 수 있다")
    void approve_ReadyStatus_Success() {
        // given
        Payment payment = createTestPayment();
        PaymentKey paymentKey = PaymentKey.of("payment-key-123");
        PaymentMethod method = PaymentMethod.CARD;
        LocalDateTime requestedAt = OffsetDateTime.parse("2024-02-13T12:17:57+09:00").toLocalDateTime();
        LocalDateTime approvedAt = OffsetDateTime.parse("2024-02-13T12:18:14+09:00").toLocalDateTime();
        String receiptUrl = "http://receipt.url";

        // when
        payment.approve(paymentKey, method, requestedAt, approvedAt, receiptUrl);

        // then
        assertThat(payment.getPaymentKey()).isEqualTo(paymentKey);
        assertThat(payment.getMethod()).isEqualTo(method);
        assertThat(payment.getRequestedAt()).isEqualTo(requestedAt);
        assertThat(payment.getApprovedAt()).isEqualTo(approvedAt);
        assertThat(payment.getReceiptUrl()).isEqualTo(receiptUrl);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.DONE);
        assertThat(payment.isApproved()).isTrue();
    }

    @Test
    @DisplayName("결제 승인 - READY 상태가 아닌 결제는 승인할 수 없다")
    void approve_NotReadyStatus_ThrowsException() {
        // given
        Payment payment = createTestPayment();
        payment.fail(); // 상태를 ABORTED로 변경

        PaymentKey paymentKey = PaymentKey.of("payment-key-123");
        PaymentMethod method = PaymentMethod.CARD;
        LocalDateTime requestedAt = OffsetDateTime.parse("2024-02-13T12:17:57+09:00").toLocalDateTime();
        LocalDateTime approvedAt = OffsetDateTime.parse("2024-02-13T12:18:14+09:00").toLocalDateTime();
        String receiptUrl = "http://receipt.url";

        // when & then
        assertThatThrownBy(() -> payment.approve(paymentKey, method, LocalDateTime.now(), LocalDateTime.now(), receiptUrl))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("준비 상태의 결제만 승인할 수 있습니다.");
    }

    @Test
    @DisplayName("결제 취소 - DONE 상태의 결제를 취소할 수 있다")
    void cancel_DoneStatus_Success() {
        // given
        Payment payment = createApprovedPayment();
        String reason = "고객 요청";

        // when
        payment.cancel(reason);

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
    }

    @Test
    @DisplayName("결제 취소 - 취소할 수 없는 상태의 결제는 취소할 수 없다")
    void cancel_CannotCancelStatus_ThrowsException() {
        // given
        Payment payment = createTestPayment(); // READY 상태
        String reason = "고객 요청";

        // when & then
        assertThatThrownBy(() -> payment.cancel(reason))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("취소할 수 없는 결제 상태입니다.");
    }

    @Test
    @DisplayName("결제 실패 - 결제를 실패 상태로 변경할 수 있다")
    void fail_Success() {
        // given
        Payment payment = createTestPayment();

        // when
        payment.fail();

        // then
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.ABORTED);
        assertThat(payment.isApproved()).isFalse();
    }

    @Test
    @DisplayName("재구성 생성자 - 모든 파라미터로 Payment를 재구성할 수 있다")
    void reconstructorPayment_AllParameters_Success() {
        // given
        PaymentKey paymentKey = PaymentKey.of("payment-key-123");
        Long orderId = 123L;
        String orderName = "테스트 주문";
        Money amount = Money.of(10000);
        String customerName = "홍길동";
        String customerEmail = "test@example.com";
        PaymentStatus status = PaymentStatus.DONE;
        PaymentMethod method = PaymentMethod.CARD;
        LocalDateTime requestedAt = OffsetDateTime.parse("2024-02-13T12:17:57+09:00").toLocalDateTime();
        LocalDateTime approvedAt = OffsetDateTime.parse("2024-02-13T12:18:14+09:00").toLocalDateTime();
        String receiptUrl = "http://receipt.url";
        String callbackUrl = "http://callback.url";
        String failUrl = "http://fail.url";
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        LocalDateTime updatedAt = LocalDateTime.now();

        // when
        Payment payment = new Payment(paymentKey, orderId, orderName, amount, customerName, customerEmail,
                status, method, requestedAt, approvedAt, receiptUrl, callbackUrl, failUrl, createdAt, updatedAt);

        // then
        assertThat(payment.getPaymentKey()).isEqualTo(paymentKey);
        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getOrderName()).isEqualTo(orderName);
        assertThat(payment.getAmount()).isEqualTo(amount);
        assertThat(payment.getCustomerName()).isEqualTo(customerName);
        assertThat(payment.getCustomerEmail()).isEqualTo(customerEmail);
        assertThat(payment.getStatus()).isEqualTo(status);
        assertThat(payment.getMethod()).isEqualTo(method);
        assertThat(payment.getRequestedAt()).isEqualTo(requestedAt);
        assertThat(payment.getApprovedAt()).isEqualTo(approvedAt);
        assertThat(payment.getReceiptUrl()).isEqualTo(receiptUrl);
        assertThat(payment.getCallbackUrl()).isEqualTo(callbackUrl);
        assertThat(payment.getFailUrl()).isEqualTo(failUrl);
        assertThat(payment.getCreatedAt()).isEqualTo(createdAt);
        assertThat(payment.getUpdatedAt()).isEqualTo(updatedAt);
    }

    private Payment createTestPayment() {
        return new Payment(
                123L,
                "테스트 주문",
                Money.of(10000),
                "홍길동",
                "test@example.com",
                "http://callback.url",
                "http://fail.url"
        );
    }

    private Payment createApprovedPayment() {
        Payment payment = createTestPayment();
        payment.approve(
                PaymentKey.of("payment-key-123"),
                PaymentMethod.CARD,
                OffsetDateTime.parse("2024-02-13T12:17:57+09:00").toLocalDateTime(),
                OffsetDateTime.parse("2024-02-13T12:18:14+09:00").toLocalDateTime(),
                "http://receipt.url"
        );
        return payment;
    }
}
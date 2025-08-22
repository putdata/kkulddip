package com.kkulddip.payment.domain.model.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PaymentStatus enum 테스트")
class PaymentStatusTest {

    @Test
    @DisplayName("결제 상태 설명 - 각 결제 상태는 올바른 설명을 가진다")
    void getDescription_AllStatuses_HasCorrectDescription() {
        // when & then
        assertThat(PaymentStatus.READY.getDescription()).isEqualTo("결제 준비");
        assertThat(PaymentStatus.IN_PROGRESS.getDescription()).isEqualTo("결제 진행중");
        assertThat(PaymentStatus.WAITING_FOR_DEPOSIT.getDescription()).isEqualTo("입금 대기");
        assertThat(PaymentStatus.DONE.getDescription()).isEqualTo("결제 완료");
        assertThat(PaymentStatus.CANCELED.getDescription()).isEqualTo("결제 취소");
        assertThat(PaymentStatus.PARTIAL_CANCELED.getDescription()).isEqualTo("부분 취소");
        assertThat(PaymentStatus.ABORTED.getDescription()).isEqualTo("결제 중단");
        assertThat(PaymentStatus.EXPIRED.getDescription()).isEqualTo("결제 만료");
    }

    @Test
    @DisplayName("완료 상태 확인 - DONE 상태만 완료된 것으로 판단한다")
    void isCompleted_DoneStatus_ReturnsTrue() {
        // when & then
        assertThat(PaymentStatus.DONE.isCompleted()).isTrue();
        assertThat(PaymentStatus.READY.isCompleted()).isFalse();
        assertThat(PaymentStatus.IN_PROGRESS.isCompleted()).isFalse();
        assertThat(PaymentStatus.WAITING_FOR_DEPOSIT.isCompleted()).isFalse();
        assertThat(PaymentStatus.CANCELED.isCompleted()).isFalse();
        assertThat(PaymentStatus.PARTIAL_CANCELED.isCompleted()).isFalse();
        assertThat(PaymentStatus.ABORTED.isCompleted()).isFalse();
        assertThat(PaymentStatus.EXPIRED.isCompleted()).isFalse();
    }

    @Test
    @DisplayName("취소 상태 확인 - CANCELED와 PARTIAL_CANCELED가 취소된 것으로 판단한다")
    void isCanceled_CanceledStatuses_ReturnsTrue() {
        // when & then
        assertThat(PaymentStatus.CANCELED.isCanceled()).isTrue();
        assertThat(PaymentStatus.PARTIAL_CANCELED.isCanceled()).isTrue();
        
        assertThat(PaymentStatus.READY.isCanceled()).isFalse();
        assertThat(PaymentStatus.IN_PROGRESS.isCanceled()).isFalse();
        assertThat(PaymentStatus.WAITING_FOR_DEPOSIT.isCanceled()).isFalse();
        assertThat(PaymentStatus.DONE.isCanceled()).isFalse();
        assertThat(PaymentStatus.ABORTED.isCanceled()).isFalse();
        assertThat(PaymentStatus.EXPIRED.isCanceled()).isFalse();
    }

    @Test
    @DisplayName("취소 가능 여부 확인 - DONE 상태만 취소 가능하다")
    void canCancel_DoneStatus_ReturnsTrue() {
        // when & then
        assertThat(PaymentStatus.DONE.canCancel()).isTrue();
        
        assertThat(PaymentStatus.READY.canCancel()).isFalse();
        assertThat(PaymentStatus.IN_PROGRESS.canCancel()).isFalse();
        assertThat(PaymentStatus.WAITING_FOR_DEPOSIT.canCancel()).isFalse();
        assertThat(PaymentStatus.CANCELED.canCancel()).isFalse();
        assertThat(PaymentStatus.PARTIAL_CANCELED.canCancel()).isFalse();
        assertThat(PaymentStatus.ABORTED.canCancel()).isFalse();
        assertThat(PaymentStatus.EXPIRED.canCancel()).isFalse();
    }

    @Test
    @DisplayName("모든 결제 상태 확인 - 정의된 모든 결제 상태가 존재한다")
    void allPaymentStatuses_Exist() {
        // given
        PaymentStatus[] allStatuses = PaymentStatus.values();

        // when & then
        assertThat(allStatuses).hasSize(8);
        assertThat(allStatuses).contains(
                PaymentStatus.READY,
                PaymentStatus.IN_PROGRESS,
                PaymentStatus.WAITING_FOR_DEPOSIT,
                PaymentStatus.DONE,
                PaymentStatus.CANCELED,
                PaymentStatus.PARTIAL_CANCELED,
                PaymentStatus.ABORTED,
                PaymentStatus.EXPIRED
        );
    }

    @Test
    @DisplayName("상태별 특성 확인 - 각 상태는 올바른 특성을 가진다")
    void statusCharacteristics_AllStatuses_HasCorrectCharacteristics() {
        // READY
        assertThat(PaymentStatus.READY.isCompleted()).isFalse();
        assertThat(PaymentStatus.READY.isCanceled()).isFalse();
        assertThat(PaymentStatus.READY.canCancel()).isFalse();

        // IN_PROGRESS
        assertThat(PaymentStatus.IN_PROGRESS.isCompleted()).isFalse();
        assertThat(PaymentStatus.IN_PROGRESS.isCanceled()).isFalse();
        assertThat(PaymentStatus.IN_PROGRESS.canCancel()).isFalse();

        // WAITING_FOR_DEPOSIT
        assertThat(PaymentStatus.WAITING_FOR_DEPOSIT.isCompleted()).isFalse();
        assertThat(PaymentStatus.WAITING_FOR_DEPOSIT.isCanceled()).isFalse();
        assertThat(PaymentStatus.WAITING_FOR_DEPOSIT.canCancel()).isFalse();

        // DONE
        assertThat(PaymentStatus.DONE.isCompleted()).isTrue();
        assertThat(PaymentStatus.DONE.isCanceled()).isFalse();
        assertThat(PaymentStatus.DONE.canCancel()).isTrue();

        // CANCELED
        assertThat(PaymentStatus.CANCELED.isCompleted()).isFalse();
        assertThat(PaymentStatus.CANCELED.isCanceled()).isTrue();
        assertThat(PaymentStatus.CANCELED.canCancel()).isFalse();

        // PARTIAL_CANCELED
        assertThat(PaymentStatus.PARTIAL_CANCELED.isCompleted()).isFalse();
        assertThat(PaymentStatus.PARTIAL_CANCELED.isCanceled()).isTrue();
        assertThat(PaymentStatus.PARTIAL_CANCELED.canCancel()).isFalse();

        // ABORTED
        assertThat(PaymentStatus.ABORTED.isCompleted()).isFalse();
        assertThat(PaymentStatus.ABORTED.isCanceled()).isFalse();
        assertThat(PaymentStatus.ABORTED.canCancel()).isFalse();

        // EXPIRED
        assertThat(PaymentStatus.EXPIRED.isCompleted()).isFalse();
        assertThat(PaymentStatus.EXPIRED.isCanceled()).isFalse();
        assertThat(PaymentStatus.EXPIRED.canCancel()).isFalse();
    }
}
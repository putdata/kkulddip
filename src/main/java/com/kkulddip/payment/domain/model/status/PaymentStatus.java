package com.kkulddip.payment.domain.model.status;

public enum PaymentStatus {
    READY("결제 준비"),
    IN_PROGRESS("결제 진행중"),
    WAITING_FOR_DEPOSIT("입금 대기"),
    DONE("결제 완료"),
    CANCELED("결제 취소"),
    PARTIAL_CANCELED("부분 취소"),
    ABORTED("결제 중단"),
    EXPIRED("결제 만료");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return this == DONE;
    }

    public boolean isCanceled() {
        return this == CANCELED || this == PARTIAL_CANCELED;
    }

    public boolean canCancel() {
        return this == DONE;
    }
}
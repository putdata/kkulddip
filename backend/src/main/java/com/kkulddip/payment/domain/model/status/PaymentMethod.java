package com.kkulddip.payment.domain.model.status;

public enum PaymentMethod {
    CARD("카드"),
    SIMPLE_PAY("간편결제"),
    VIRTUAL_ACCOUNT("가상계좌"),
    TRANSFER("계좌이체"),
    MOBILE_PHONE("휴대폰"),
    CULTURE_GIFT_CERTIFICATE("문화상품권"),
    BOOK_GIFT_CERTIFICATE("도서문화상품권"),
    GAME_GIFT_CERTIFICATE("게임문화상품권"),
    TOSS_PAY("토스페이"),
    PAYCO("페이코"),
    KAKAO_PAY("카카오페이"),
    NAVER_PAY("네이버페이"),
    SAMSUNG_PAY("삼성페이"),
    APPLE_PAY("애플페이");

    private final String description;

    PaymentMethod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // 토스페이먼츠 API 응답값으로부터 enum 찾기 (안전한 변환)
    public static PaymentMethod fromString(String method) {
        if (method == null) {
            return null;
        }

        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            if (paymentMethod.name().equals(method) ||
                    paymentMethod.description.equals(method)) {
                return paymentMethod;
            }
        }

        // 매핑되지 않은 결제 수단의 경우 기본값 반환
        return CARD;
    }
}
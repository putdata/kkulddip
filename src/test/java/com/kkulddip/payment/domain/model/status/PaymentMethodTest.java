package com.kkulddip.payment.domain.model.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PaymentMethod enum 테스트")
class PaymentMethodTest {

    @Test
    @DisplayName("결제 수단 설명 - 각 결제 수단은 올바른 설명을 가진다")
    void getDescription_AllMethods_HasCorrectDescription() {
        // when & then
        assertThat(PaymentMethod.CARD.getDescription()).isEqualTo("카드");
        assertThat(PaymentMethod.SIMPLE_PAY.getDescription()).isEqualTo("간편결제");
        assertThat(PaymentMethod.VIRTUAL_ACCOUNT.getDescription()).isEqualTo("가상계좌");
        assertThat(PaymentMethod.TRANSFER.getDescription()).isEqualTo("계좌이체");
        assertThat(PaymentMethod.MOBILE_PHONE.getDescription()).isEqualTo("휴대폰");
        assertThat(PaymentMethod.CULTURE_GIFT_CERTIFICATE.getDescription()).isEqualTo("문화상품권");
        assertThat(PaymentMethod.BOOK_GIFT_CERTIFICATE.getDescription()).isEqualTo("도서문화상품권");
        assertThat(PaymentMethod.GAME_GIFT_CERTIFICATE.getDescription()).isEqualTo("게임문화상품권");
        assertThat(PaymentMethod.TOSS_PAY.getDescription()).isEqualTo("토스페이");
        assertThat(PaymentMethod.PAYCO.getDescription()).isEqualTo("페이코");
        assertThat(PaymentMethod.KAKAO_PAY.getDescription()).isEqualTo("카카오페이");
        assertThat(PaymentMethod.NAVER_PAY.getDescription()).isEqualTo("네이버페이");
        assertThat(PaymentMethod.SAMSUNG_PAY.getDescription()).isEqualTo("삼성페이");
        assertThat(PaymentMethod.APPLE_PAY.getDescription()).isEqualTo("애플페이");
    }

    @Test
    @DisplayName("문자열로부터 변환 - enum name으로 PaymentMethod를 찾을 수 있다")
    void fromString_EnumName_ReturnsCorrectMethod() {
        // when & then
        assertThat(PaymentMethod.fromString("CARD")).isEqualTo(PaymentMethod.CARD);
        assertThat(PaymentMethod.fromString("SIMPLE_PAY")).isEqualTo(PaymentMethod.SIMPLE_PAY);
        assertThat(PaymentMethod.fromString("KAKAO_PAY")).isEqualTo(PaymentMethod.KAKAO_PAY);
        assertThat(PaymentMethod.fromString("TOSS_PAY")).isEqualTo(PaymentMethod.TOSS_PAY);
    }

    @Test
    @DisplayName("문자열로부터 변환 - description으로 PaymentMethod를 찾을 수 있다")
    void fromString_Description_ReturnsCorrectMethod() {
        // when & then
        assertThat(PaymentMethod.fromString("카드")).isEqualTo(PaymentMethod.CARD);
        assertThat(PaymentMethod.fromString("간편결제")).isEqualTo(PaymentMethod.SIMPLE_PAY);
        assertThat(PaymentMethod.fromString("카카오페이")).isEqualTo(PaymentMethod.KAKAO_PAY);
        assertThat(PaymentMethod.fromString("토스페이")).isEqualTo(PaymentMethod.TOSS_PAY);
    }

    @Test
    @DisplayName("문자열로부터 변환 - null 값이면 null을 반환한다")
    void fromString_NullValue_ReturnsNull() {
        // when
        PaymentMethod result = PaymentMethod.fromString(null);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("문자열로부터 변환 - 매핑되지 않은 값이면 기본값 CARD를 반환한다")
    void fromString_UnmappedValue_ReturnsDefaultCard() {
        // when & then
        assertThat(PaymentMethod.fromString("UNKNOWN_METHOD")).isEqualTo(PaymentMethod.CARD);
        assertThat(PaymentMethod.fromString("알 수 없는 결제수단")).isEqualTo(PaymentMethod.CARD);
        assertThat(PaymentMethod.fromString("")).isEqualTo(PaymentMethod.CARD);
        assertThat(PaymentMethod.fromString("   ")).isEqualTo(PaymentMethod.CARD);
    }

    @Test
    @DisplayName("문자열로부터 변환 - 대소문자 구분하여 처리한다")
    void fromString_CaseSensitive_HandlesCorrectly() {
        // when & then
        assertThat(PaymentMethod.fromString("card")).isEqualTo(PaymentMethod.CARD); // 소문자는 기본값
        assertThat(PaymentMethod.fromString("Card")).isEqualTo(PaymentMethod.CARD); // 대소문자 혼합은 기본값
        assertThat(PaymentMethod.fromString("CARD")).isEqualTo(PaymentMethod.CARD); // 정확한 매칭
    }

    @Test
    @DisplayName("모든 결제 수단 확인 - 정의된 모든 결제 수단이 존재한다")
    void allPaymentMethods_Exist() {
        // given
        PaymentMethod[] allMethods = PaymentMethod.values();

        // when & then
        assertThat(allMethods).hasSize(14);
        assertThat(allMethods).contains(
                PaymentMethod.CARD,
                PaymentMethod.SIMPLE_PAY,
                PaymentMethod.VIRTUAL_ACCOUNT,
                PaymentMethod.TRANSFER,
                PaymentMethod.MOBILE_PHONE,
                PaymentMethod.CULTURE_GIFT_CERTIFICATE,
                PaymentMethod.BOOK_GIFT_CERTIFICATE,
                PaymentMethod.GAME_GIFT_CERTIFICATE,
                PaymentMethod.TOSS_PAY,
                PaymentMethod.PAYCO,
                PaymentMethod.KAKAO_PAY,
                PaymentMethod.NAVER_PAY,
                PaymentMethod.SAMSUNG_PAY,
                PaymentMethod.APPLE_PAY
        );
    }
}
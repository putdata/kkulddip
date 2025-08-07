package com.kkulddip.payment.infrastructure.external.toss.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class TossOrderIdConverterTest {

    @Test
    void toTossOrderId_정상적인_Long값_변환() {
        // given
        Long orderId = 12345L;
        
        // when
        String result = TossOrderIdConverter.toTossOrderId(orderId);
        
        // then
        assertThat(result).isEqualTo("ORDER-12345");
    }
    
    @Test
    void toTossOrderId_null값_예외발생() {
        // when & then
        assertThatThrownBy(() -> TossOrderIdConverter.toTossOrderId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("orderId는 null일 수 없습니다.");
    }
    
    @Test
    void fromTossOrderId_정상적인_토스형식_변환() {
        // given
        String tossOrderId = "ORDER-12345";
        
        // when
        Long result = TossOrderIdConverter.fromTossOrderId(tossOrderId);
        
        // then
        assertThat(result).isEqualTo(12345L);
    }
    
    @Test
    void fromTossOrderId_잘못된_형식_예외발생() {
        // when & then
        assertThatThrownBy(() -> TossOrderIdConverter.fromTossOrderId("12345"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("올바르지 않은 토스 orderId 형식입니다: 12345");
    }
    
    @Test
    void fromTossOrderId_null값_예외발생() {
        // when & then
        assertThatThrownBy(() -> TossOrderIdConverter.fromTossOrderId(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("올바르지 않은 토스 orderId 형식입니다: null");
    }
    
    @Test
    void fromTossOrderId_숫자변환_실패_예외발생() {
        // when & then
        assertThatThrownBy(() -> TossOrderIdConverter.fromTossOrderId("ORDER-abc"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("orderId 숫자 변환 실패: abc");
    }
    
    @Test
    void isTossOrderIdFormat_정상적인_토스형식_true() {
        // when & then
        assertThat(TossOrderIdConverter.isTossOrderIdFormat("ORDER-12345")).isTrue();
    }
    
    @Test
    void isTossOrderIdFormat_일반형식_false() {
        // when & then
        assertThat(TossOrderIdConverter.isTossOrderIdFormat("12345")).isFalse();
    }
    
    @Test
    void isTossOrderIdFormat_null값_false() {
        // when & then
        assertThat(TossOrderIdConverter.isTossOrderIdFormat(null)).isFalse();
    }
    
    @Test
    void 양방향_변환_테스트() {
        // given
        Long originalOrderId = 99999L;
        
        // when
        String tossOrderId = TossOrderIdConverter.toTossOrderId(originalOrderId);
        Long convertedOrderId = TossOrderIdConverter.fromTossOrderId(tossOrderId);
        
        // then
        assertThat(convertedOrderId).isEqualTo(originalOrderId);
        assertThat(tossOrderId).isEqualTo("ORDER-99999");
    }
}
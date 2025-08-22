package com.kkulddip.order.domain.model.vo;

/**
 * 절약 값 계산 결과
 * 
 * @param savedMoney 절약된 금액
 * @param savedCo2 절약된 CO2량 (g 단위)
 */
public record SavingsResult(
    Money savedMoney,
    Double savedCo2
) {
    
    /**
     * SavingsResult 생성 팩토리 메서드
     * 
     * @param savedMoney 절약된 금액
     * @param savedCo2 절약된 CO2량 (g 단위)
     * @return SavingsResult 인스턴스
     */
    public static SavingsResult of(Money savedMoney, Double savedCo2) {
        return new SavingsResult(savedMoney, savedCo2);
    }
}
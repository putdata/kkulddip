package com.kkulddip.order.domain.model.vo;

/**
 * 금액을 나타내는 값 객체
 */
public record Money(
    Integer amount
) {
    
    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("금액은 null일 수 없습니다.");
        }
        if (amount < 0) {
            throw new IllegalArgumentException("금액은 음수가 될 수 없습니다.");
        }
    }
    
    public static Money of(Integer amount) {
        return new Money(amount);
    }
    
    public Money add(Money other) {
        return new Money(this.amount + other.amount);
    }
    
    public Money subtract(Money other) {
        if (this.amount < other.amount) {
            throw new IllegalArgumentException("금액은 음수가 될 수 없습니다.");
        }
        return new Money(this.amount - other.amount);
    }
    
    public Money multiply(Integer multiplier) {
        if (multiplier < 0) {
            throw new IllegalArgumentException("금액은 0보다 작을 수 없습니다.");
        }
        return new Money(this.amount * multiplier);
    }

    public boolean isGreaterThan(Money other) {
        return this.amount > other.amount;
    }
    
    public boolean isLessThan(Money other) {
        return this.amount < other.amount;
    }

    public boolean isEqual(Money other) {
        return this.amount == other.amount;
    }
}
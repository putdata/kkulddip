package com.kkulddip.domain.customer.enums;

/**
 * 고객 레벨 enum
 */
public enum CustomerLevel {

    SPROUT_BEE(1, "새싹벌"),
    WORKER_BEE(2, "일벌"),
    HONEY_BEE(3, "꿀벌"),
    QUEEN_BEE(4, "여왕벌");

    private final int levelValue;
    private final String description;

    CustomerLevel(int levelValue, String description) {
        this.levelValue = levelValue;
        this.description = description;
    }
}
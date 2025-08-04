package com.kkulddip.notification.domain.model.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    ORDER("주문", "주문 관련 알림"),
    PICKUP("픽업", "픽업 관련 알림"),
    EVENT("이벤트", "이벤트 및 할인"),
    REVIEW("리뷰", "리뷰 관련 알림"),
    MARKETING("마케팅", "마케팅 및 광고"),
    SYSTEM("시스템", "시스템 공지");

    private final String displayName;
    private final String description;

    /**
     * 우선순위 반환 (1: 높음, 2: 보통, 3: 낮음)
     */
    public int getPriority() {
        return switch (this) {
            case ORDER, PICKUP -> 1;
            case REVIEW, SYSTEM -> 2;
            case MARKETING, EVENT -> 3;
        };
    }
}
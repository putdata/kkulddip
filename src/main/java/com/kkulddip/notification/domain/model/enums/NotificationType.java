package com.kkulddip.notification.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    EVENT("이벤트", "EVENT"),
    MARKETING("마케팅", "MARKETING"), 
    ORDER("주문", "ORDER"),
    PICKUP("픽업", "PICKUP"),
    REVIEW("리뷰", "REVIEW"),
    SYSTEM("시스템", "SYSTEM");

    private final String description;
    private final String value;
}
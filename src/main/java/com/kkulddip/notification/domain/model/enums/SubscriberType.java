package com.kkulddip.notification.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriberType {
    ALL("전체", "ALL"),
    CUSTOMER("고객", "CUSTOMER"),
    OWNER("사장", "OWNER"),
    SPECIFIC("특정 사용자", "SPECIFIC");

    private final String description;
    private final String value;
}
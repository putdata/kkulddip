package com.kkulddip.notification.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriberType {
    ALL("전체", "ALL"),
    CUSTOMER("고객", "CUSTOMER"),
    OWNER("사장", "OWNER"),
    STORE("가게", "STORE");

    private final String description;
    private final String value;
}
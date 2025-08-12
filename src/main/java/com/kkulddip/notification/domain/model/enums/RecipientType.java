package com.kkulddip.notification.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecipientType {
    CUSTOMER("고객", "CUSTOMER"),
    OWNER("사장", "OWNER");

    private final String description;
    private final String value;
}
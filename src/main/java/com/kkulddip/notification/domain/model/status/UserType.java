package com.kkulddip.notification.domain.model.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserType {
    CUSTOMER("고객", "customer"),
    OWNER("사장", "owner");

    private final String displayName;
    private final String code;
}
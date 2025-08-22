package com.kkulddip.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    CUSTOMER("고객", "CUSTOMER"),
    OWNER("사장", "OWNER"),
    ADMIN("관리자", "ADMIN");

    private final String description;
    private final String authority;
}
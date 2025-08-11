package com.kkulddip.notification.domain.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PublisherType {
    ADMIN("관리자", "ADMIN"),
    OWNER("사장", "OWNER"), 
    SYSTEM("시스템", "SYSTEM");

    private final String description;
    private final String value;
}
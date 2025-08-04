package com.kkulddip.notification.domain.model.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriberType {
    ALL("전체", "모든 사용자"),
    CUSTOMER("고객", "고객만"),
    OWNER("사장", "사장만"),
    SPECIFIC("특정", "특정 사용자");

    private final String displayName;
    private final String description;
}
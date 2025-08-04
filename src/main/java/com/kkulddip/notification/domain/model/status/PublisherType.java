package com.kkulddip.notification.domain.model.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PublisherType {
    SYSTEM("시스템", "시스템"),
    OWNER("사장", "매장"),
    ADMIN("관리자", "관리자");

    private final String displayName;
    private final String description;
}
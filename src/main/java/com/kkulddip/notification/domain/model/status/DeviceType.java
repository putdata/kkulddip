package com.kkulddip.notification.domain.model.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DeviceType {
    WEB("웹", "web"),
    ANDROID("안드로이드", "android"),
    IOS("iOS", "ios");

    private final String displayName;
    private final String platformCode;
}
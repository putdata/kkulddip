package com.kkulddip.common.enums;

/**
 * OAuth2 제공자 열거형
 */
public enum OAuth2Provider {
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver");

    private final String registrationId;

    OAuth2Provider(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public static OAuth2Provider fromRegistrationId(String registrationId) {
        for (OAuth2Provider provider : values()) {
            if (provider.getRegistrationId().equals(registrationId)) {
                return provider;
            }
        }
        throw new IllegalArgumentException("Unknown OAuth2 provider: " + registrationId);
    }
}
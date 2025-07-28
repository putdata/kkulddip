package com.kkulddip.common.entity;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class User {
    private String email;
    private String name;
    private String profileImageUrl;
    private String oauth2ProviderId;
}
package com.kkulddip.domain.user.entity;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.common.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public abstract class User {

    @Column(nullable = false, unique = true)
    protected String email;

    @Column
    protected String name;

    @Column(name = "profile_image_url")
    protected String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth2_provider")
    protected OAuth2Provider oauth2Provider;

    @Column(name = "oauth2_provider_id")
    protected String oauth2ProviderId;

    public abstract Long getId();

    public UserRole getRole() {
        return switch (this.getClass().getSimpleName()) {
            case "Customer" -> UserRole.CUSTOMER;
            case "Owner" -> UserRole.OWNER;
            default -> throw new IllegalStateException("Unknown user type: " + this.getClass().getName());
        };
    }
}

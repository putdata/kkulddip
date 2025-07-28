package com.kkulddip.domain.user.entity;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.common.enums.UserRole;
import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.owner.entity.Owner;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@Getter
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
        if (this instanceof Customer) {
            return UserRole.CUSTOMER;
        }
        if (this instanceof Owner) {
            return UserRole.OWNER;
        }
        throw new IllegalStateException("Unknown user type: " + this.getClass().getName());
    }

    /**
     * 프로필 업데이트 요청 검증
     */
    public static void validateProfileUpdate(String name, String profileImageUrl) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
        
        if (name.length() > 50) {
            throw new IllegalArgumentException("이름은 50자를 초과할 수 없습니다.");
        }
        
        if (profileImageUrl != null && profileImageUrl.length() > 500) {
            throw new IllegalArgumentException("프로필 이미지 URL은 500자를 초과할 수 없습니다.");
        }
    }
}

package com.kkulddip.domain.user.entity;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.common.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
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

    /**
     * OAuth2 프로필 정보 업데이트 공통 로직
     * @param newName 새로운 이름
     * @param newProfileImageUrl 새로운 프로필 이미지 URL
     * @return 업데이트 여부
     */
    public boolean updateOAuth2Profile(String newName, String newProfileImageUrl) {
        boolean updated = false;
        
        if (newName != null && !newName.equals(this.name)) {
            this.name = newName;
            updated = true;
        }
        
        if (newProfileImageUrl != null && !newProfileImageUrl.equals(this.profileImageUrl)) {
            this.profileImageUrl = newProfileImageUrl;
            updated = true;
        }
        
        return updated;
    }

    /**
     * 프로필 업데이트 (검증 포함)
     * @param newName 새로운 이름
     * @param newProfileImageUrl 새로운 프로필 이미지 URL
     */
    public void updateProfile(String newName, String newProfileImageUrl) {
        validateProfileUpdate(newName, newProfileImageUrl);
        
        this.name = newName;
        this.profileImageUrl = newProfileImageUrl;
    }
}

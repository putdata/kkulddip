package com.kkulddip.notification.infrastructure.persistence.jpa.mapper;

import com.kkulddip.notification.domain.model.entity.UserToken;
import com.kkulddip.notification.infrastructure.persistence.jpa.entity.UserTokenEntity;
import org.springframework.stereotype.Component;

@Component
public class UserTokenMapper {

    /**
     * Domain -> Entity 변환
     */
    public UserTokenEntity toEntity(UserToken domain) {
        if (domain == null) {
            return null;
        }

        return UserTokenEntity.builder()
            .userTokenId(domain.getUserTokenId())
            .userId(domain.getUserId())
            .userType(domain.getUserType())
            .fcmToken(domain.getFcmToken())
            .deviceType(domain.getDeviceType())
            .isActive(domain.getIsActive())
            .lastLoginAt(domain.getLastLoginAt())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
    }

    /**
     * Entity -> Domain 변환
     */
    public UserToken toDomain(UserTokenEntity entity) {
        if (entity == null) {
            return null;
        }

        return UserToken.builder()
            .userTokenId(entity.getUserTokenId())
            .userId(entity.getUserId())
            .userType(entity.getUserType())
            .fcmToken(entity.getFcmToken())
            .deviceType(entity.getDeviceType())
            .isActive(entity.getIsActive())
            .lastLoginAt(entity.getLastLoginAt())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}
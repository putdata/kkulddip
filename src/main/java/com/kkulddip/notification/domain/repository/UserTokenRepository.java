package com.kkulddip.notification.domain.repository;

import com.kkulddip.notification.domain.model.entity.UserToken;
import com.kkulddip.notification.domain.model.status.DeviceType;
import com.kkulddip.notification.domain.model.status.UserType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserTokenRepository {

    UserToken save(UserToken userToken);

    Optional<UserToken> findById(Long userTokenId);

    Optional<UserToken> findByUserIdAndUserType(Long userId, UserType userType);

    List<UserToken> findAllActiveTokens();

    List<UserToken> findAllActiveTokensByUserType(UserType userType);

    long countActiveTokens();

    long countActiveTokensByUserType(UserType userType);

    List<UserToken> findByDeviceTypeAndIsActiveTrue(DeviceType deviceType);

    List<UserToken> findActiveTokensSince(LocalDateTime since);

    List<UserToken> findExpiredActiveTokens();

    int deleteInactiveTokensBefore(LocalDateTime before);

    List<UserToken> findActiveTokensByUserIds(List<Long> userIds, UserType userType);

    int deactivateTokens(List<String> tokens);

    void delete(UserToken userToken);

    boolean existsByFcmToken(String fcmToken);

    long countActiveTokensByDeviceType(DeviceType deviceType);
}
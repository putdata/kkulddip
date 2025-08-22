package com.kkulddip.domain.userToken.repository;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.domain.userToken.entity.UserToken;
import com.kkulddip.domain.userToken.enums.DeviceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    // 사용자별 활성 토큰 조회
    List<UserToken> findByUserIdAndUserTypeAndIsActiveTrue(Long userId, UserRole userType);

    // 사용자별 디바이스 타입 토큰 조회
    Optional<UserToken> findByUserIdAndUserTypeAndDeviceType(Long userId, UserRole userType, DeviceType deviceType);

    // 사용자 타입별 모든 활성 토큰 조회
    @Query("SELECT ut FROM UserToken ut WHERE ut.userType = :userType AND ut.isActive = true")
    List<UserToken> findAllActiveTokensByUserType(@Param("userType") UserRole userType);

    // FCM 토큰으로 조회
    Optional<UserToken> findByFcmToken(String fcmToken);

    // 비활성 토큰 정리용
    @Query("SELECT ut FROM UserToken ut WHERE ut.isActive = false OR ut.lastUsedAt < :cutoffDate")
    List<UserToken> findInactiveTokens(@Param("cutoffDate") LocalDateTime cutoffDate);
}
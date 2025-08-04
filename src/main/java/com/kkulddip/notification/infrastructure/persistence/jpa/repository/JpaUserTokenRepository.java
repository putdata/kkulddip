package com.kkulddip.notification.infrastructure.persistence.jpa.repository;

import com.kkulddip.notification.domain.model.status.UserType;
import com.kkulddip.notification.domain.model.status.DeviceType;
import com.kkulddip.notification.infrastructure.persistence.jpa.entity.UserTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JpaUserTokenRepository extends JpaRepository<UserTokenEntity, Long> {

    /**
     * 사용자 ID와 타입으로 토큰 조회
     */
    Optional<UserTokenEntity> findByUserIdAndUserType(Long userId, UserType userType);

    /**
     * 활성 토큰 전체 조회
     */
    @Query("SELECT ut FROM UserTokenEntity ut WHERE ut.isActive = true ORDER BY ut.createdAt DESC")
    List<UserTokenEntity> findAllActiveTokens();

    /**
     * 사용자 타입별 활성 토큰 조회
     */
    @Query("SELECT ut FROM UserTokenEntity ut WHERE ut.userType = :userType AND ut.isActive = true ORDER BY ut.createdAt DESC")
    List<UserTokenEntity> findAllActiveTokensByUserType(@Param("userType") UserType userType);

    /**
     * 활성 토큰 수 조회
     */
    @Query("SELECT COUNT(ut) FROM UserTokenEntity ut WHERE ut.isActive = true")
    long countActiveTokens();

    /**
     * 사용자 타입별 활성 토큰 수 조회
     */
    @Query("SELECT COUNT(ut) FROM UserTokenEntity ut WHERE ut.userType = :userType AND ut.isActive = true")
    long countActiveTokensByUserType(@Param("userType") UserType userType);

    /**
     * 디바이스 타입별 활성 토큰 조회
     */
    @Query("SELECT ut FROM UserTokenEntity ut WHERE ut.deviceType = :deviceType AND ut.isActive = true")
    List<UserTokenEntity> findByDeviceTypeAndIsActiveTrue(@Param("deviceType") DeviceType deviceType);

    /**
     * 특정 기간 이후 활성 토큰 조회
     */
    @Query("SELECT ut FROM UserTokenEntity ut WHERE ut.createdAt >= :since AND ut.isActive = true")
    List<UserTokenEntity> findActiveTokensSince(@Param("since") LocalDateTime since);

    /**
     * 오래된 비활성 토큰 삭제
     */
    @Modifying
    @Query("DELETE FROM UserTokenEntity ut WHERE ut.isActive = false AND ut.updatedAt < :before")
    int deleteInactiveTokensBefore(@Param("before") LocalDateTime before);

    /**
     * 사용자 ID 목록으로 활성 토큰 조회
     */
    @Query("SELECT ut FROM UserTokenEntity ut WHERE ut.userId IN :userIds AND ut.userType = :userType AND ut.isActive = true")
    List<UserTokenEntity> findActiveTokensByUserIds(@Param("userIds") List<Long> userIds,
                                                    @Param("userType") UserType userType);

    /**
     * 토큰 배치 비활성화
     */
    @Modifying
    @Query("UPDATE UserTokenEntity ut SET ut.isActive = false WHERE ut.fcmToken IN :tokens")
    int deactivateTokens(@Param("tokens") List<String> tokens);

    /**
     * FCM 토큰 존재 여부 확인
     */
    boolean existsByFcmTokenAndIsActiveTrue(String fcmToken);

    /**
     * FCM 토큰으로 토큰 조회
     */
    Optional<UserTokenEntity> findByFcmTokenAndIsActiveTrue(String fcmToken);

    /**
     * 디바이스 타입별 카운트
     */
    @Query("SELECT COUNT(ut) FROM UserTokenEntity ut WHERE ut.deviceType = :deviceType AND ut.isActive = true")
    long countActiveTokensByDeviceType(@Param("deviceType") DeviceType deviceType);

    /**
     * 만료된 활성 토큰 조회 (6개월 이상)
     */
    @Query("SELECT ut FROM UserTokenEntity ut WHERE ut.isActive = true AND " +
        "CASE WHEN ut.lastLoginAt IS NOT NULL THEN ut.lastLoginAt ELSE ut.createdAt END < :sixMonthsAgo")
    List<UserTokenEntity> findExpiredActiveTokens(@Param("sixMonthsAgo") LocalDateTime sixMonthsAgo);
}
package com.kkulddip.notification.infrastructure.persistence.jpa.adapter;

import com.kkulddip.notification.domain.model.status.UserType;
import com.kkulddip.notification.domain.model.status.DeviceType;
import com.kkulddip.notification.domain.model.entity.UserToken;
import com.kkulddip.notification.domain.repository.UserTokenRepository;
import com.kkulddip.notification.infrastructure.persistence.jpa.mapper.UserTokenMapper;
import com.kkulddip.notification.infrastructure.persistence.jpa.repository.JpaUserTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class UserTokenRepositoryImpl implements UserTokenRepository {

    private final JpaUserTokenRepository jpaRepository;
    private final UserTokenMapper mapper;

    @Override
    public UserToken save(UserToken userToken) {
        var entity = mapper.toEntity(userToken);
        var savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<UserToken> findById(Long userTokenId) {
        return jpaRepository.findById(userTokenId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<UserToken> findByUserIdAndUserType(Long userId, UserType userType) {
        return jpaRepository.findByUserIdAndUserType(userId, userType)
            .map(mapper::toDomain);
    }

    @Override
    public List<UserToken> findAllActiveTokens() {
        return jpaRepository.findAllActiveTokens()
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserToken> findAllActiveTokensByUserType(UserType userType) {
        return jpaRepository.findAllActiveTokensByUserType(userType)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public long countActiveTokens() {
        return jpaRepository.countActiveTokens();
    }

    @Override
    public long countActiveTokensByUserType(UserType userType) {
        return jpaRepository.countActiveTokensByUserType(userType);
    }

    @Override
    public List<UserToken> findByDeviceTypeAndIsActiveTrue(DeviceType deviceType) {
        return jpaRepository.findByDeviceTypeAndIsActiveTrue(deviceType)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserToken> findActiveTokensSince(LocalDateTime since) {
        return jpaRepository.findActiveTokensSince(since)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserToken> findExpiredActiveTokens() {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6);
        return jpaRepository.findExpiredActiveTokens(sixMonthsAgo)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public int deleteInactiveTokensBefore(LocalDateTime before) {
        return jpaRepository.deleteInactiveTokensBefore(before);
    }

    @Override
    public List<UserToken> findActiveTokensByUserIds(List<Long> userIds, UserType userType) {
        return jpaRepository.findActiveTokensByUserIds(userIds, userType)
            .stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public int deactivateTokens(List<String> tokens) {
        return jpaRepository.deactivateTokens(tokens);
    }

    @Override
    public void delete(UserToken userToken) {
        var entity = mapper.toEntity(userToken);
        jpaRepository.delete(entity);
    }

    @Override
    public boolean existsByFcmToken(String fcmToken) {
        return jpaRepository.existsByFcmTokenAndIsActiveTrue(fcmToken);
    }

    @Override
    public long countActiveTokensByDeviceType(DeviceType deviceType) {
        return jpaRepository.countActiveTokensByDeviceType(deviceType);
    }
}
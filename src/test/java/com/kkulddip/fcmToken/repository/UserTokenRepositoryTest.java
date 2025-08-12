package com.kkulddip.fcmToken.repository;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.domain.userToken.entity.UserToken;
import com.kkulddip.domain.userToken.enums.DeviceType;
import com.kkulddip.domain.userToken.repository.UserTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import com.kkulddip.common.config.JpaAuditingConfig;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(
    type = FilterType.ASSIGNABLE_TYPE, 
    classes = {UserTokenRepository.class}
))
@Import(JpaAuditingConfig.class)
@ActiveProfiles("citest")
@DisplayName("UserTokenRepository 테스트")
class UserTokenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserTokenRepository userTokenRepository;

    private UserToken customerToken1;
    private UserToken customerToken2;
    private UserToken ownerToken1;
    private UserToken inactiveToken;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oldDate = now.minusDays(10);

        customerToken1 = UserToken.builder()
            .userId(1L)
            .userType(UserRole.CUSTOMER)
            .fcmToken("customer-android-token")
            .deviceType(DeviceType.ANDROID)
            .build();

        customerToken2 = UserToken.builder()
            .userId(1L)
            .userType(UserRole.CUSTOMER)
            .fcmToken("customer-web-token")
            .deviceType(DeviceType.WEB)
            .build();

        ownerToken1 = UserToken.builder()
            .userId(2L)
            .userType(UserRole.OWNER)
            .fcmToken("owner-ios-token")
            .deviceType(DeviceType.IOS)
            .build();

        inactiveToken = UserToken.builder()
            .userId(3L)
            .userType(UserRole.CUSTOMER)
            .fcmToken("inactive-token")
            .deviceType(DeviceType.ANDROID)
            .build();

        entityManager.persistAndFlush(customerToken1);
        entityManager.persistAndFlush(customerToken2);
        entityManager.persistAndFlush(ownerToken1);
        entityManager.persistAndFlush(inactiveToken);

        // 비활성화 및 오래된 토큰 설정
        inactiveToken.deactivate();
        entityManager.persistAndFlush(inactiveToken);
    }

    @Test
    @DisplayName("사용자별 활성 토큰 조회 - 성공")
    void findByUserIdAndUserTypeAndIsActiveTrue_Success() {
        // when
        List<UserToken> activeTokens = userTokenRepository
            .findByUserIdAndUserTypeAndIsActiveTrue(1L, UserRole.CUSTOMER);

        // then
        assertThat(activeTokens).hasSize(2);
        assertThat(activeTokens).extracting(UserToken::getFcmToken)
            .containsExactlyInAnyOrder("customer-android-token", "customer-web-token");
        assertThat(activeTokens).allMatch(UserToken::getIsActive);
    }

    @Test
    @DisplayName("사용자별 디바이스 타입 토큰 조회 - 성공")
    void findByUserIdAndUserTypeAndDeviceType_Success() {
        // when
        Optional<UserToken> token = userTokenRepository
            .findByUserIdAndUserTypeAndDeviceType(1L, UserRole.CUSTOMER, DeviceType.ANDROID);

        // then
        assertThat(token).isPresent();
        assertThat(token.get().getFcmToken()).isEqualTo("customer-android-token");
        assertThat(token.get().getDeviceType()).isEqualTo(DeviceType.ANDROID);
    }

    @Test
    @DisplayName("사용자별 디바이스 타입 토큰 조회 - 존재하지 않는 경우")
    void findByUserIdAndUserTypeAndDeviceType_NotFound() {
        // when
        Optional<UserToken> token = userTokenRepository
            .findByUserIdAndUserTypeAndDeviceType(1L, UserRole.CUSTOMER, DeviceType.IOS);

        // then
        assertThat(token).isEmpty();
    }

    @Test
    @DisplayName("사용자 타입별 모든 활성 토큰 조회 - 성공")
    void findAllActiveTokensByUserType_Success() {
        // when
        List<UserToken> customerTokens = userTokenRepository
            .findAllActiveTokensByUserType(UserRole.CUSTOMER);

        // then
        assertThat(customerTokens).hasSize(2);
        assertThat(customerTokens).extracting(UserToken::getUserType)
            .containsOnly(UserRole.CUSTOMER);
        assertThat(customerTokens).allMatch(UserToken::getIsActive);
    }

    @Test
    @DisplayName("FCM 토큰으로 조회 - 성공")
    void findByFcmToken_Success() {
        // when
        Optional<UserToken> token = userTokenRepository
            .findByFcmToken("customer-android-token");

        // then
        assertThat(token).isPresent();
        assertThat(token.get().getUserId()).isEqualTo(1L);
        assertThat(token.get().getDeviceType()).isEqualTo(DeviceType.ANDROID);
    }

    @Test
    @DisplayName("FCM 토큰으로 조회 - 존재하지 않는 토큰")
    void findByFcmToken_NotFound() {
        // when
        Optional<UserToken> token = userTokenRepository
            .findByFcmToken("non-existent-token");

        // then
        assertThat(token).isEmpty();
    }

    @Test
    @DisplayName("비활성 토큰 조회 - 성공")
    void findInactiveTokens_Success() {
        // given
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(5);

        // when
        List<UserToken> inactiveTokens = userTokenRepository
            .findInactiveTokens(cutoffDate);

        // then
        assertThat(inactiveTokens).hasSize(1);
        assertThat(inactiveTokens.get(0).getFcmToken()).isEqualTo("inactive-token");
        assertThat(inactiveTokens.get(0).getIsActive()).isFalse();
    }

    @Test
    @DisplayName("토큰 저장 및 고유 제약조건 테스트")
    void saveToken_UniqueConstraint_Test() {
        // given
        UserToken newToken = UserToken.builder()
            .userId(4L)
            .userType(UserRole.OWNER)
            .fcmToken("unique-new-token")
            .deviceType(DeviceType.WEB)
            .build();

        // when
        UserToken savedToken = userTokenRepository.save(newToken);

        // then
        assertThat(savedToken.getTokenId()).isNotNull();
        assertThat(savedToken.getFcmToken()).isEqualTo("unique-new-token");
    }

    @Test
    @DisplayName("토큰 삭제 테스트")
    void deleteToken_Success() {
        // given
        Long tokenId = customerToken1.getTokenId();

        // when
        userTokenRepository.delete(customerToken1);
        entityManager.flush();

        // then
        Optional<UserToken> deletedToken = userTokenRepository.findById(tokenId);
        assertThat(deletedToken).isEmpty();
    }

    @Test
    @DisplayName("여러 토큰 일괄 삭제 테스트")
    void deleteAllTokens_Success() {
        // given
        List<UserToken> tokensToDelete = List.of(customerToken1, customerToken2);

        // when
        userTokenRepository.deleteAll(tokensToDelete);
        entityManager.flush();

        // then
        List<UserToken> remainingTokens = userTokenRepository.findAll();
        assertThat(remainingTokens).hasSize(2);
        assertThat(remainingTokens).extracting(UserToken::getFcmToken)
            .containsExactlyInAnyOrder("owner-ios-token", "inactive-token");
    }

    @Test
    @DisplayName("사용자 타입이 다른 경우 토큰 조회 불가")
    void findByDifferentUserType_NotFound() {
        // when
        List<UserToken> tokens = userTokenRepository
            .findByUserIdAndUserTypeAndIsActiveTrue(1L, UserRole.OWNER);

        // then
        assertThat(tokens).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 조회")
    void findByNonExistentUserId_NotFound() {
        // when
        List<UserToken> tokens = userTokenRepository
            .findByUserIdAndUserTypeAndIsActiveTrue(999L, UserRole.CUSTOMER);

        // then
        assertThat(tokens).isEmpty();
    }

    @Test
    @DisplayName("ADMIN 사용자 타입 토큰 조회")
    void findAdminTokens_Success() {
        // given
        UserToken adminToken = UserToken.builder()
            .userId(100L)
            .userType(UserRole.ADMIN)
            .fcmToken("admin-token")
            .deviceType(DeviceType.WEB)
            .build();
        entityManager.persistAndFlush(adminToken);

        // when
        List<UserToken> adminTokens = userTokenRepository
            .findAllActiveTokensByUserType(UserRole.ADMIN);

        // then
        assertThat(adminTokens).hasSize(1);
        assertThat(adminTokens.get(0).getUserType()).isEqualTo(UserRole.ADMIN);
    }
}
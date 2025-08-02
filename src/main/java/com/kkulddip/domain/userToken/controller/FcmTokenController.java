package com.kkulddip.domain.userToken.controller;

import com.kkulddip.common.enums.OAuth2Provider;
import com.kkulddip.common.enums.UserRole;
import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.domain.customer.entity.Customer;
import com.kkulddip.domain.customer.repository.CustomerRepository;
import com.kkulddip.domain.owner.entity.Owner;
import com.kkulddip.domain.owner.repository.OwnerRepository;
import com.kkulddip.domain.userToken.dto.request.FcmTokenRequest;
import com.kkulddip.domain.userToken.service.FcmTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FCM 토큰 관리 컨트롤러
 * Firebase Cloud Messaging 토큰의 등록, 비활성화, 삭제 기능을 제공
 *
 * @author 이석규
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/fcm-tokens")
public class FcmTokenController implements FcmTokenApi {

    private final FcmTokenService fcmTokenService;
    private final CustomerRepository customerRepository;
    private final OwnerRepository ownerRepository;

    /**
     * FCM 토큰을 등록하거나 업데이트합니다.
     * JwtUserInfo 사용 이유
     *  - 보안: 클라이언트가 임의의 userId를 조작할 수 없음
     *  - 인증 보장: JWT 토큰 검증을 통한 신뢰할 수 있는 사용자 정보
     *  - 표준: Spring Security 표준 방식
     *
     * @param userInfo 인증된 사용자 정보
     * @param request FCM 토큰 등록 요청
     * @return 등록 결과
     */
    @Override
    @PostMapping
    public ApiResponse<Void> registerToken(
            @AuthenticationPrincipal JwtUserInfo userInfo,
            @Valid @RequestBody FcmTokenRequest request
    ) {
        log.info("FCM 토큰 등록 요청 - username: {}, deviceType: {}", 
                userInfo.username(), request.deviceType());

        UserRole userRole = UserRole.valueOf(userInfo.role());
        Long userId = getUserId(userInfo, userRole);
        fcmTokenService.registerOrUpdateToken(userId, userRole, request);

        return ApiResponse.of();
    }

    /**
     * 현재 사용자의 모든 FCM 토큰을 비활성화합니다.
     *
     * @param userInfo 인증된 사용자 정보
     * @return 비활성화 결과
     */
    @PostMapping("/deactivate")
    public ApiResponse<Void> deactivateCurrentUserTokens(
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        log.info("FCM 토큰 비활성화 요청 - username: {}", userInfo.username());

        UserRole userRole = UserRole.valueOf(userInfo.role());
        Long userId = getUserId(userInfo, userRole);
        fcmTokenService.deactivateUserTokens(userId, userRole);

        return ApiResponse.of();
    }

    /**
     * 특정 사용자의 FCM 토큰을 비활성화합니다. (관리자용)
     *
     * @param userId 대상 사용자 ID
     * @return 비활성화 결과
     */
    @Override
    @PostMapping("/users/{userId}/deactivate")
    public ApiResponse<Void> deactivateToken(@PathVariable Long userId) {
        log.info("사용자 FCM 토큰 비활성화 요청 - targetUserId: {}", userId);

        // TODO: 관리자 권한 체크 로직 추가 필요
        // 사용자 역할 자동 감지 및 토큰 비활성화
        UserRole userRole = getUserRoleById(userId);
        fcmTokenService.deactivateUserTokens(userId, userRole);

        return ApiResponse.of();
    }

    /**
     * 특정 FCM 토큰을 완전히 삭제합니다.
     *
     * @param fcmToken 삭제할 FCM 토큰
     * @return 삭제 결과
     */
    @Override
    @DeleteMapping("/{fcmToken}")
    public ApiResponse<Void> deleteToken(@PathVariable String fcmToken) {
        log.info("FCM 토큰 삭제 요청 - fcmToken: {}", fcmToken);

        boolean deleted = fcmTokenService.deleteToken(fcmToken);
        
        if (deleted) {
            return ApiResponse.of();
        } else {
            return ApiResponse.of(404);
        }
    }

    /**
     * JwtUserInfo에서 실제 userId를 조회합니다.
     *
     * @param userInfo JWT 사용자 정보
     * @param userRole 사용자 역할
     * @return 사용자 ID
     */
    private Long getUserId(JwtUserInfo userInfo, UserRole userRole) {
        OAuth2Provider provider = OAuth2Provider.valueOf(userInfo.oauth2Provider().toUpperCase());
        String providerId = userInfo.oauth2ProviderId();

        return switch (userRole) {
            case CUSTOMER -> {
                Customer customer = customerRepository
                    .findByOauth2ProviderAndOauth2ProviderId(provider, providerId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, 
                        "Customer not found with OAuth2 info: " + provider + ", " + providerId));
                yield customer.getId();
            }
            case OWNER -> {
                Owner owner = ownerRepository
                    .findByOauth2ProviderAndOauth2ProviderId(provider, providerId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND,
                        "Owner not found with OAuth2 info: " + provider + ", " + providerId));
                yield owner.getId();
            }
            case ADMIN -> throw new BusinessException(ErrorCode.USER_NOT_FOUND, 
                "Admin role not supported for FCM token registration");
        };
    }

    /**
     * 사용자 ID로 UserRole을 자동 감지합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자 역할
     */
    private UserRole getUserRoleById(Long userId) {
        // Customer 테이블에서 먼저 조회
        if (customerRepository.existsById(userId)) {
            return UserRole.CUSTOMER;
        }
        
        // Owner 테이블에서 조회
        if (ownerRepository.existsById(userId)) {
            return UserRole.OWNER;
        }
        
        // 둘 다 없으면 예외 발생
        throw new BusinessException(ErrorCode.USER_NOT_FOUND, 
            "User not found with ID: " + userId);
    }
}
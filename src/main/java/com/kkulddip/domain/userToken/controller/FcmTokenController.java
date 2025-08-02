package com.kkulddip.domain.userToken.controller;

import com.kkulddip.common.enums.UserRole;
import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.domain.userToken.dto.request.FcmTokenRequest;
import com.kkulddip.domain.userToken.service.UserTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FCM 토큰 관리 컨트롤러
 * Firebase Cloud Messaging 토큰의 등록, 비활성화, 삭제 기능을 제공합니다.
 *
 * @author 이석규
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/fcm-tokens")
public class FcmTokenController implements FcmTokenApi {

    private final UserTokenService userTokenService;

    /**
     * FCM 토큰을 등록하거나 업데이트합니다.
     *
     * @param userInfo 인증된 사용자 정보
     * @param request FCM 토큰 등록 요청
     * @return 등록 결과
     */
    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> registerToken(
            @AuthenticationPrincipal JwtUserInfo userInfo,
            @Valid @RequestBody FcmTokenRequest request
    ) {
        log.info("FCM 토큰 등록 요청 - username: {}, deviceType: {}", 
                userInfo.username(), request.deviceType());

        UserRole userRole = UserRole.valueOf(userInfo.role());
        // TODO: username으로 실제 userId 조회 로직 필요
        Long userId = 1L; // 임시값
        userTokenService.registerOrUpdateToken(userId, userRole, request);

        return ResponseEntity.ok(
            ApiResponse.<Void>builder()
                .success(true)
                .status(200)
                .build()
        );
    }

    /**
     * 현재 사용자의 모든 FCM 토큰을 비활성화합니다.
     *
     * @param userInfo 인증된 사용자 정보
     * @return 비활성화 결과
     */
    @PostMapping("/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateCurrentUserTokens(
            @AuthenticationPrincipal JwtUserInfo userInfo
    ) {
        log.info("FCM 토큰 비활성화 요청 - username: {}", userInfo.username());

        UserRole userRole = UserRole.valueOf(userInfo.role());
        // TODO: username으로 실제 userId 조회 로직 필요
        Long userId = 1L; // 임시값
        userTokenService.deactivateUserTokens(userId, userRole);

        return ResponseEntity.ok(
            ApiResponse.<Void>builder()
                .success(true)
                .status(200)
                .build()
        );
    }

    /**
     * 특정 사용자의 FCM 토큰을 비활성화합니다. (관리자용)
     *
     * @param userId 대상 사용자 ID
     * @return 비활성화 결과
     */
    @Override
    @PostMapping("/users/{userId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateToken(@PathVariable Long userId) {
        log.info("사용자 FCM 토큰 비활성화 요청 - targetUserId: {}", userId);

        // TODO: 관리자 권한 체크 로직 추가 필요
        // 현재는 CUSTOMER 기본값으로 처리 (추후 사용자 조회를 통해 실제 UserRole 확인 필요)
        userTokenService.deactivateUserTokens(userId, UserRole.CUSTOMER);

        return ResponseEntity.ok(
            ApiResponse.<Void>builder()
                .success(true)
                .status(200)
                .build()
        );
    }

    /**
     * 특정 FCM 토큰을 완전히 삭제합니다.
     *
     * @param fcmToken 삭제할 FCM 토큰
     * @return 삭제 결과
     */
    @Override
    @DeleteMapping("/{fcmToken}")
    public ResponseEntity<ApiResponse<Void>> deleteToken(@PathVariable String fcmToken) {
        log.info("FCM 토큰 삭제 요청 - fcmToken: {}", fcmToken);

        boolean deleted = userTokenService.deleteToken(fcmToken);
        
        if (deleted) {
            return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                    .success(true)
                    .status(200)
                    .build()
            );
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
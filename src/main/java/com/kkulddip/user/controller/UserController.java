package com.kkulddip.user.controller;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.user.dto.request.UserCreateRequest;
import com.kkulddip.user.dto.request.UserUpdateRequest;
import com.kkulddip.user.dto.response.UserResponse;
import com.kkulddip.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserResponse userResponse = userService.createUser(request);
        return ApiResponse.of(201, userResponse);
    }

    @Override
    public ApiResponse<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse userResponse = userService.getUserById(userId);
        return ApiResponse.of(userResponse);
    }

    @Override
    public ApiResponse<?> getUsers(
        @RequestParam(required = false) Boolean isVerified,
        @RequestParam(required = false) Boolean notificationEnabled,
        @RequestParam(required = false) String phoneNumber,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        // 페이징 요청이 있는지 확인 (page나 size 파라미터가 있으면 페이징으로 처리)
        boolean isPagingRequest = pageable.getPageNumber() != 0 || pageable.getPageSize() != 20 || pageable.getSort().isSorted();
        
        // 필터 개수 계산
        int filterCount = 0;
        if (isVerified != null) filterCount++;
        if (notificationEnabled != null) filterCount++;
        if (phoneNumber != null) filterCount++;
        
        // 복합 필터링은 아직 지원하지 않음 (추후 구현 예정)
        if (filterCount > 1) {
            throw new BusinessException(ErrorCode.COMMON_INVALID_INPUT, "복합 필터링은 현재 지원되지 않습니다. 하나의 필터만 사용해주세요.");
        }
        
        if (isPagingRequest && filterCount == 0) {
            // 페이징만 요청된 경우
            Page<UserResponse> users = userService.getAllUsers(pageable);
            return ApiResponse.of(users);
        } else if (phoneNumber != null) {
            // 전화번호로 조회 (사용자가 없으면 빈 배열 반환)
            try {
                UserResponse user = userService.getUserByPhoneNumber(phoneNumber);
                return ApiResponse.of(List.of(user));
            } catch (BusinessException e) {
                if (e.getErrorCode() == ErrorCode.USER_NOT_FOUND) {
                    return ApiResponse.of(List.of()); // 빈 배열 반환
                }
                throw e; // 다른 예외는 그대로 전파
            }
        } else if (isVerified != null) {
            // 인증 상태 필터링
            List<UserResponse> users = userService.getUsersByVerificationStatus(isVerified);
            return ApiResponse.of(users);
        } else if (notificationEnabled != null) {
            // 알림 설정 필터링
            List<UserResponse> users = userService.getUsersByNotificationEnabled(notificationEnabled);
            return ApiResponse.of(users);
        } else {
            // 단순 전체 조회
            List<UserResponse> users = userService.getAllUsers();
            return ApiResponse.of(users);
        }
    }

    @Override
    public ApiResponse<UserResponse> updateUser(
        @PathVariable Long userId,
        @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse userResponse = userService.updateUser(userId, request);
        return ApiResponse.of(userResponse);
    }

    @Override
    public ApiResponse<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.of(204);
    }
} 
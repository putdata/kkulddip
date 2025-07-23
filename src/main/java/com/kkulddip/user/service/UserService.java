package com.kkulddip.user.service;

import com.kkulddip.common.exception.BusinessException;
import com.kkulddip.common.exception.ErrorCode;
import com.kkulddip.user.dto.request.UserCreateRequest;
import com.kkulddip.user.dto.request.UserUpdateRequest;
import com.kkulddip.user.dto.response.UserResponse;
import com.kkulddip.user.entity.User;
import com.kkulddip.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        validatePhoneNumberNotExists(request.phoneNumber());
        
        User savedUser = userRepository.save(
            User.builder()
                .phoneNumber(request.phoneNumber())
                .isVerified(false)
                .notificationEnabled(request.notificationEnabled())
                .build()
        );
        return UserResponse.from(savedUser);
    }

    public UserResponse getUserById(Long userId) {
        User user = findUserById(userId);
        return UserResponse.from(user);
    }

    public UserResponse getUserByPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserResponse.from(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
            .map(UserResponse::from)
            .toList();
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(UserResponse::from);
    }

    public List<UserResponse> getUsersByVerificationStatus(Boolean isVerified) {
        List<User> users = userRepository.findByIsVerified(isVerified);
        return users.stream()
            .map(UserResponse::from)
            .toList();
    }

    public List<UserResponse> getUsersByNotificationEnabled(Boolean notificationEnabled) {
        List<User> users = userRepository.findByNotificationEnabled(notificationEnabled);
        return users.stream()
            .map(UserResponse::from)
            .toList();
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = findUserById(userId);
        
        if (request.phoneNumber() != null && !request.phoneNumber().equals(user.getPhoneNumber())) {
            validatePhoneNumberNotExists(request.phoneNumber());
            user.updatePhoneNumber(request.phoneNumber());
        }
        
        if (request.isVerified() != null) {
            user.updateVerificationStatus(request.isVerified());
        }
        
        if (request.notificationEnabled() != null) {
            user.updateNotificationEnabled(request.notificationEnabled());
        }
        
        return UserResponse.from(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = findUserById(userId);
        userRepository.delete(user);
    }

    @Transactional
    public UserResponse verifyUser(Long userId) {
        User user = findUserById(userId);
        user.updateVerificationStatus(true);
        return UserResponse.from(user);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private void validatePhoneNumberNotExists(String phoneNumber) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new BusinessException(ErrorCode.DUPLICATE_PHONE_NUMBER);
        }
    }
} 
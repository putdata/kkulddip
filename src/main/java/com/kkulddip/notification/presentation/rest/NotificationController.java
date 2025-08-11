package com.kkulddip.notification.presentation.rest;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.common.security.jwt.JwtUserInfo;
import com.kkulddip.notification.application.dto.request.NotificationRequest;
import com.kkulddip.notification.application.dto.response.NotificationResponse;
import com.kkulddip.notification.application.service.NotificationService;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 알림 관리 REST API 컨트롤러
 *
 * @author Claude
 * @since 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

    private final NotificationService notificationService;

    @PostMapping
    @Override
    public ApiResponse<NotificationResponse> createNotification(
        @Valid @RequestBody NotificationRequest request) {
        
        log.info("알림 생성 요청 - title: {}, subscriberType: {}", 
            request.getTitle(), request.getSubscriberType());

        NotificationResponse response = notificationService.createNotification(request);
        return ApiResponse.of(201, response);
    }


    @GetMapping
    @Override
    public ApiResponse<List<NotificationResponse>> getNotificationsBySubscriber(
        @RequestParam Long subscriberId,
        @RequestParam SubscriberType subscriberType,
        @AuthenticationPrincipal JwtUserInfo userInfo) {
        
        Long authenticatedUserId = Long.valueOf(userInfo.userId());
        String authenticatedRole = userInfo.role();

        List<NotificationResponse> notifications = notificationService
            .getNotificationsBySubscriberWithAuth(subscriberId, subscriberType, authenticatedUserId, authenticatedRole);
        
        return ApiResponse.of(notifications);
    }

}
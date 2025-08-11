package com.kkulddip.notification.presentation.rest;

import com.kkulddip.common.response.ApiResponse;
import com.kkulddip.notification.application.dto.request.NotificationRequest;
import com.kkulddip.notification.application.dto.response.NotificationResponse;
import com.kkulddip.notification.application.service.NotificationService;
import com.kkulddip.notification.domain.model.enums.SubscriberType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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
        Authentication authentication) {
        
        // JWT에서 사용자 정보 추출
        @SuppressWarnings("unchecked")
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        Long authenticatedUserId = Long.valueOf(details.get("user_id").toString());
        String authenticatedRole = authentication.getAuthorities().iterator().next().getAuthority()
            .replace("ROLE_", ""); // ROLE_ 접두사 제거

        List<NotificationResponse> notifications = notificationService
            .getNotificationsBySubscriberWithAuth(subscriberId, subscriberType, authenticatedUserId, authenticatedRole);
        
        return ApiResponse.of(notifications);
    }

}
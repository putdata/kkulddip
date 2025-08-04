package com.kkulddip.notification.infrastructure.external.firebase;

import com.kkulddip.notification.domain.model.entity.Notification;

public interface FirebaseMessagingService {
    boolean sendMessage(String fcmToken, Notification notification);
}
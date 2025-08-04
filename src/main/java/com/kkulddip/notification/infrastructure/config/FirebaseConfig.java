package com.kkulddip.notification.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-file:}")
    private Resource serviceAccountFile;

    @Value("${app.notification.mock-mode:false}")
    private boolean mockMode;

    private boolean firebaseInitialized = false;

    @PostConstruct
    public void initialize() {
        try {
            // Mock 모드인 경우 Firebase 초기화 스킵
            if (mockMode) {
                log.info("Mock 모드가 활성화되어 Firebase 초기화를 스킵합니다.");
                return;
            }

            // 서비스 계정 파일 확인
            if (serviceAccountFile == null || !serviceAccountFile.exists()) {
                log.warn("Firebase 서비스 계정 파일이 없습니다. Mock 모드로 전환합니다.");
                return;
            }

            if (!serviceAccountFile.isReadable()) {
                log.warn("Firebase 서비스 계정 파일을 읽을 수 없습니다. Mock 모드로 전환합니다.");
                return;
            }

            if (FirebaseApp.getApps().isEmpty()) {
                try (InputStream serviceAccount = serviceAccountFile.getInputStream()) {
                    FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                    FirebaseApp.initializeApp(options);
                    firebaseInitialized = true;
                    log.info("✅ Firebase application initialized successfully");
                }
            } else {
                firebaseInitialized = true;
                log.info("✅ Firebase application already initialized");
            }
        } catch (IOException e) {
            log.error("❌ Firebase 초기화 실패: {}", e.getMessage());
            log.warn("🔄 Mock 모드로 전환합니다.");
            firebaseInitialized = false;
        } catch (Exception e) {
            log.error("❌ Firebase 초기화 중 예상치 못한 오류: {}", e.getMessage(), e);
            firebaseInitialized = false;
        }
    }

    /**
     * Firebase 초기화 상태 확인
     */
    public boolean isFirebaseInitialized() {
        return firebaseInitialized && !FirebaseApp.getApps().isEmpty();
    }
}
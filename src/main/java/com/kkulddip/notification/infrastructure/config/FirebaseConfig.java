package com.kkulddip.notification.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Firebase 설정 클래스
 *
 * <p>Firebase Admin SDK를 초기화하고 FirebaseMessaging Bean을 생성합니다.</p>
 *
 * @author Claude
 * @since 1.0
 */
@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-key:}")
    private String serviceAccountKeyPath;

    @Value("${firebase.project-id:}")
    private String projectId;

    /**
     * Firebase 애플리케이션을 초기화합니다.
     */
    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                log.info("Firebase 초기화 시작 - serviceAccountKeyPath: {}", serviceAccountKeyPath);

                FileInputStream serviceAccount = new FileInputStream(serviceAccountKeyPath);
                
                FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount));

                // 프로젝트 ID가 설정되어 있으면 명시적으로 설정
                if (projectId != null && !projectId.trim().isEmpty()) {
                    optionsBuilder.setProjectId(projectId);
                }

                FirebaseOptions options = optionsBuilder.build();
                
                FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);
                
                log.info("Firebase 초기화 완료 - app name: {}, project id: {}", 
                    firebaseApp.getName(), firebaseApp.getOptions().getProjectId());
                    
            } else {
                log.info("Firebase가 이미 초기화되어 있습니다.");
            }
            
        } catch (IOException e) {
            log.error("Firebase 초기화 실패 - 서비스 계정 키 파일을 찾을 수 없습니다: {}", 
                serviceAccountKeyPath, e);
            throw new RuntimeException("Firebase 초기화 실패", e);
        } catch (Exception e) {
            log.error("Firebase 초기화 중 예상치 못한 오류 발생", e);
            throw new RuntimeException("Firebase 초기화 실패", e);
        }
    }

    /**
     * FirebaseMessaging Bean을 생성합니다.
     *
     * @return FirebaseMessaging 인스턴스
     */
    @Bean
    public FirebaseMessaging firebaseMessaging() {
        try {
            FirebaseApp firebaseApp = FirebaseApp.getInstance();
            log.debug("FirebaseMessaging Bean 생성 완료");
            return FirebaseMessaging.getInstance(firebaseApp);
        } catch (IllegalStateException e) {
            log.error("FirebaseApp이 초기화되지 않았습니다.", e);
            throw new RuntimeException("FirebaseMessaging Bean 생성 실패", e);
        }
    }
}
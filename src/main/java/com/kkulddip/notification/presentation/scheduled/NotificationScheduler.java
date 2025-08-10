package com.kkulddip.notification.presentation.scheduled;

import com.kkulddip.notification.application.facade.NotificationProcessingFacade;
import com.kkulddip.notification.infrastructure.persistence.redis.RedisNotificationQueueService;
import com.kkulddip.notification.presentation.dto.request.NotificationRequest;
import com.kkulddip.notification.presentation.dto.response.NotificationResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty(
    value = "spring.task.scheduling.enabled",
    havingValue = "true",
    matchIfMissing = true
)
@Profile("!citest") // citest 프로파일에서는 실행하지 않음
public class NotificationScheduler {

    private final RedisNotificationQueueService redisNotificationQueueService;
    private final NotificationProcessingFacade notificationProcessingFacade;

    /**
     * Redis ZSet에서 알림 요청 조회 및 처리 (매 10초마다 실행)
     */
    @Scheduled(fixedRateString = "${scheduler.process-rate}") // 1초마다 실행
    public void processNotificationQueue() {
        try {
            // Redis ZSet 크기 확인
            long queueSize = redisNotificationQueueService.getQueueSize();

            if (queueSize == 0) {
                return;
            }

            log.info("=== Redis ZSet 알림 처리 시작 ===");
            log.info("대기 중인 알림 요청: {}개", queueSize);

            // 한 번에 최대 50개씩 처리
            int batchSize = Math.min(50, (int) queueSize);
            Set<NotificationRequest> requests = redisNotificationQueueService
                .popNotificationRequests(batchSize);

            if (requests.isEmpty()) {
                log.debug("처리할 알림 요청이 없습니다.");
                return;
            }

            log.info("배치 처리 시작: {}개 알림 요청", requests.size());

            int successCount = 0;
            int failureCount = 0;

            // 각 알림 요청 처리
            for (NotificationRequest request : requests) {
                try {
                    log.debug("알림 요청 처리 시작: 제목={}, 대상={}",
                        request.getTitle(), request.getSubscriberType());

                    NotificationResponse response = notificationProcessingFacade
                        .processNotificationRequest(request);

                    if (response.isSuccess()) {
                        successCount++;
                        log.debug("알림 요청 처리 성공: 제목={}", request.getTitle());
                    } else {
                        failureCount++;
                        log.warn("알림 요청 처리 실패: 제목={}, 오류={}",
                            request.getTitle(), response.getMessage());
                    }

                } catch (Exception e) {
                    failureCount++;
                    log.error("알림 요청 처리 중 예외 발생: 제목={}, 오류={}",
                        request.getTitle(), e.getMessage(), e);
                }
            }

            log.info("=== Redis ZSet 알림 처리 완료 ===");
            log.info("처리 결과: 성공={}개, 실패={}개", successCount, failureCount);
            log.info("남은 대기 알림: {}개", redisNotificationQueueService.getQueueSize());

        } catch (Exception e) {
            log.error("Redis ZSet 알림 처리 중 전체 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 큐 상태 모니터링 (매 1분마다 실행)
     */
    @Scheduled(fixedRateString = "${scheduler.monitor-rate}") // 1분마다 실행
    public void monitorQueueStatus() {
        try {
            long queueSize = redisNotificationQueueService.getQueueSize();

            if (queueSize > 0) {
                log.info("📊 알림 큐 상태: 대기 중인 요청 {}개", queueSize);

                // 큐가 100개 이상이면 경고 로그
                if (queueSize > 100) {
                    log.warn("⚠️ 알림 큐에 {}개의 요청이 쌓여있습니다. 처리 성능을 확인해주세요.", queueSize);
                }

                // 개발 환경에서는 큐 내용 출력
                if (log.isDebugEnabled() && queueSize <= 10) {
                    redisNotificationQueueService.logQueueContents();
                }
            }

        } catch (Exception e) {
            log.error("큐 상태 모니터링 중 오류: {}", e.getMessage());
        }
    }

    /**
     * 통계 로깅 (매 10분마다 실행)
     */
    @Scheduled(fixedRateString = "${scheduler.statistics-rate}") // 10분마다 실행
    public void logStatistics() {
        try {
            var statistics = notificationProcessingFacade.getStatistics();

            log.info("=== 📈 알림 서비스 통계 ===");
            log.info("전체 사용자: {}명 (고객: {}명, 사장: {}명)",
                statistics.getTotalUsers(),
                statistics.getTotalCustomers(),
                statistics.getTotalOwners());
            log.info("오늘 발송: 성공 {}건, 실패 {}건, 성공률 {:.1f}%",
                statistics.getTodaySuccess(),
                statistics.getTodayFailed(),
                statistics.getSuccessRate());

        } catch (Exception e) {
            log.error("통계 로깅 중 오류: {}", e.getMessage());
        }
    }

    /**
     * 서비스 상태 체크 (매 5분마다 실행)
     */
    @Scheduled(fixedRateString = "${scheduler.healthcheck-rate}") // 5분마다 실행
    public void healthCheck() {
        try {
            log.debug("🔍 알림 서비스 상태 체크 시작");

            // Redis 연결 체크
            long queueSize = redisNotificationQueueService.getQueueSize();
            log.debug("✅ Redis 연결 정상 (큐 크기: {})", queueSize);

            // 기본 통계 조회로 DB 연결 체크
            var statistics = notificationProcessingFacade.getStatistics();
            log.debug("✅ Database 연결 정상 (활성 사용자: {}명)", statistics.getTotalUsers());

            log.debug("✅ 알림 서비스 상태 체크 완료 - 모든 시스템 정상");

        } catch (Exception e) {
            log.error("❌ 알림 서비스 상태 체크 실패: {}", e.getMessage());
            // 필요시 알람이나 모니터링 시스템에 통지
        }
    }

    /**
     * 테스트용 알림 (개발 환경에서만 활성화)
     * 주석 해제 시 매 30초마다 테스트 알림을 Redis에 추가
     */
    /*
    @Scheduled(fixedRate = 30000) // 30초마다 실행
    @Profile("dev") // 개발 환경에서만 실행
    public void generateTestNotification() {
        try {
            NotificationRequest testRequest = NotificationRequest.builder()
                    .title("테스트 알림 " + System.currentTimeMillis())
                    .content("스케줄러에서 생성된 테스트 알림입니다.")
                    .subscriberType(SubscriberType.ALL)
                    .notificationType(NotificationType.SYSTEM)
                    .createdAt(LocalDateTime.now())
                    .build();

            redisNotificationQueueService.addNotificationRequest(testRequest);
            log.info("🧪 테스트 알림 생성: {}", testRequest.getTitle());

        } catch (Exception e) {
            log.error("테스트 알림 생성 실패: {}", e.getMessage());
        }
    }
    */
}
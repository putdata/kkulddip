package com.kkulddip.notification.interfaces.event.testUtil;

import com.kkulddip.notification.interfaces.dto.request.NotificationRequest;
import com.kkulddip.notification.domain.model.status.NotificationType;
import com.kkulddip.notification.domain.model.status.PublisherType;
import com.kkulddip.notification.domain.model.status.SubscriberType;
import com.kkulddip.notification.infrastructure.persistence.redis.RedisNotificationQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Redis ZSet에 테스트용 알림 데이터를 생성하는 유틸리티 클래스
 *
 * 사용법:
 * 1. @Component 주석 해제하고 애플리케이션 실행 시 자동 실행
 * 2. 또는 REST API로 수동 호출
 */
@Slf4j
@RequiredArgsConstructor
@Profile("develop") // local 프로파일에서만 활성화
@Component // 자동 실행을 원하면 주석 해제
public class RedisTestDataGenerator implements CommandLineRunner {

    public final RedisNotificationQueueService redisNotificationQueueService;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Redis 테스트 데이터 생성 시작...");

        // 기존 큐 정리
        clearQueue();

        // 기본 테스트 데이터 생성
        generateBasicTestData();

        log.info("✅ Redis 테스트 데이터 생성 완료!");
        printQueueStatus();
    }

    /**
     * 기존 큐 정리
     */
    public void clearQueue() {
        try {
            // 큐 크기 확인
            long beforeSize = redisNotificationQueueService.getQueueSize();

            if (beforeSize > 0) {
                log.info("🧹 기존 큐 정리 중... (기존 데이터: {}개)", beforeSize);
                // Redis ZSet 전체 삭제는 RedisTemplate을 직접 사용해야 함
                // 또는 모든 데이터를 pop하여 정리
                redisNotificationQueueService.popNotificationRequests((int)beforeSize);
                log.info("✅ 기존 큐 정리 완료");
            }
        } catch (Exception e) {
            log.warn("⚠️ 큐 정리 중 오류 발생: {}", e.getMessage());
        }
    }

    /**
     * 기본 테스트 데이터 생성 (5개)
     */
    public void generateBasicTestData() {
        log.info("📝 기본 테스트 데이터 생성 중...");

        // 1. 전체 사용자 대상 시스템 알림
        addSystemMaintenanceNotification();

        // 2. 고객 대상 이벤트 알림
        addCustomerEventNotification();

        // 3. 사장 대상 리뷰 알림
        addOwnerReviewNotification();

        // 4. 특정 고객 대상 주문 알림
        addSpecificCustomerOrderNotification();

        // 5. 특정 사장 대상 새 주문 알림
        addSpecificOwnerNewOrderNotification();

        log.info("✅ 기본 테스트 데이터 5개 생성 완료");
    }

    /**
     * 1. 전체 사용자 대상 시스템 점검 알림
     */
    private void addSystemMaintenanceNotification() {
        NotificationRequest request = NotificationRequest.builder()
            .title("🔧 시스템 점검 안내")
            .content("더 나은 서비스 제공을 위해 오늘 밤 2시부터 4시까지 시스템 점검을 진행합니다. 이용에 불편을 드려 죄송합니다.")
            .subscriberType(SubscriberType.ALL)
            .notificationType(NotificationType.SYSTEM)
            .publisherType(PublisherType.SYSTEM)
            .actionUrl("/notices/maintenance")
            .createdAt(LocalDateTime.now())
            .build();

        redisNotificationQueueService.addNotificationRequest(request);
        log.debug("➕ 시스템 점검 알림 추가 완료");
    }

    /**
     * 2. 고객 대상 이벤트 알림
     */
    private void addCustomerEventNotification() {
        NotificationRequest request = NotificationRequest.builder()
            .title("🎉 주말 특가 이벤트!")
            .content("이번 주말 한정! 모든 메뉴 20% 할인! 맛있는 음식을 더 저렴하게 즐기세요!")
            .subscriberType(SubscriberType.CUSTOMER)
            .notificationType(NotificationType.EVENT)
            .publisherType(PublisherType.ADMIN)
            .actionUrl("/events/weekend-special")
            .createdAt(LocalDateTime.now().plusSeconds(1))
            .build();

        redisNotificationQueueService.addNotificationRequest(request);
        log.debug("➕ 고객 이벤트 알림 추가 완료");
    }

    /**
     * 3. 사장 대상 리뷰 알림
     */
    private void addOwnerReviewNotification() {
        NotificationRequest request = NotificationRequest.builder()
            .title("⭐ 새로운 5점 리뷰!")
            .content("김고객님이 \"정말 맛있어요! 배송도 빠르고 포장도 깔끔해요!\"라는 5점 리뷰를 남겨주셨습니다.")
            .subscriberType(SubscriberType.OWNER)
            .notificationType(NotificationType.REVIEW)
            .publisherType(PublisherType.SYSTEM)
            .actionUrl("/reviews/latest")
            .createdAt(LocalDateTime.now().plusSeconds(2))
            .build();

        redisNotificationQueueService.addNotificationRequest(request);
        log.debug("➕ 사장 리뷰 알림 추가 완료");
    }

    /**
     * 4. 특정 고객 대상 주문 완료 알림
     */
    private void addSpecificCustomerOrderNotification() {
        Long customerId = 1001L;
        Long orderId = 98765L + System.currentTimeMillis() % 1000;

        NotificationRequest request = NotificationRequest.builder()
            .title("🍔 주문 조리 완료!")
            .content("주문하신 치킨버거 세트가 조리 완료되었습니다. 픽업 가능합니다!")
            .subscriberType(SubscriberType.SPECIFIC)
            .subscriberId(customerId)
            .notificationType(NotificationType.ORDER)
            .publisherType(PublisherType.OWNER)
            .publisherId(2001L)
            .actionUrl("/orders/" + orderId)
            .createdAt(LocalDateTime.now().plusSeconds(3))
            .build();

        redisNotificationQueueService.addNotificationRequest(request);
        log.debug("➕ 특정 고객 주문 알림 추가 완료 (고객 ID: {})", customerId);
    }

    /**
     * 5. 특정 사장 대상 새 주문 알림
     */
    private void addSpecificOwnerNewOrderNotification() {
        Long ownerId = 2001L;
        Long customerId = 1002L;
        Long orderId = 98766L + System.currentTimeMillis() % 1000;

        NotificationRequest request = NotificationRequest.builder()
            .title("🔔 새로운 주문 접수!")
            .content("김고객님이 불고기버거 세트를 주문하셨습니다. 총 주문금액: 15,000원")
            .subscriberType(SubscriberType.SPECIFIC)
            .subscriberId(ownerId)
            .notificationType(NotificationType.ORDER)
            .publisherType(PublisherType.SYSTEM)
            .actionUrl("/admin/orders/" + orderId)
            .createdAt(LocalDateTime.now().plusSeconds(4))
            .build();

        redisNotificationQueueService.addNotificationRequest(request);
        log.debug("➕ 특정 사장 신규주문 알림 추가 완료 (사장 ID: {})", ownerId);
    }

    /**
     * 대용량 테스트 데이터 생성 (개수 지정)
     */
    public void generateMassTestData(int count) {
        log.info("📝 대용량 테스트 데이터 생성 중... ({}개)", count);

        for (int i = 1; i <= count; i++) {
            NotificationRequest request = NotificationRequest.builder()
                .title("대용량 테스트 알림 #" + i)
                .content("배치 처리 성능 테스트를 위한 " + i + "번째 알림입니다.")
                .subscriberType(i % 3 == 0 ? SubscriberType.ALL :
                    i % 3 == 1 ? SubscriberType.CUSTOMER : SubscriberType.OWNER)
                .notificationType(NotificationType.SYSTEM)
                .publisherType(PublisherType.ADMIN)
                .createdAt(LocalDateTime.now().plusSeconds(i))
                .build();

            redisNotificationQueueService.addNotificationRequest(request);

            // 진행률 출력 (20개마다)
            if (i % 20 == 0) {
                log.debug("   📝 {}개 추가됨 ({:.1f}%)", i, (double) i / count * 100);
            }
        }

        log.info("✅ 대용량 테스트 데이터 {}개 생성 완료", count);
    }

    /**
     * 오류 상황 테스트 데이터 생성
     */
    public void generateErrorTestData() {
        log.info("📝 오류 상황 테스트 데이터 생성 중...");

        // 1. 존재하지 않는 사용자 ID
        NotificationRequest invalidUserRequest = NotificationRequest.builder()
            .title("존재하지 않는 사용자 테스트")
            .content("이 알림은 발송 대상을 찾을 수 없어 실패해야 합니다.")
            .subscriberType(SubscriberType.SPECIFIC)
            .subscriberId(99999L) // 존재하지 않는 ID
            .notificationType(NotificationType.ORDER)
            .publisherType(PublisherType.SYSTEM)
            .createdAt(LocalDateTime.now().plusSeconds(10))
            .build();

        redisNotificationQueueService.addNotificationRequest(invalidUserRequest);

        // 2. 빈 제목/내용 (유효성 검사 실패)
        NotificationRequest emptyContentRequest = NotificationRequest.builder()
            .title("") // 빈 제목
            .content("   ") // 공백만 있는 내용
            .subscriberType(SubscriberType.ALL)
            .notificationType(NotificationType.SYSTEM)
            .publisherType(PublisherType.SYSTEM)
            .createdAt(LocalDateTime.now().plusSeconds(11))
            .build();

        redisNotificationQueueService.addNotificationRequest(emptyContentRequest);

        log.info("✅ 오류 상황 테스트 데이터 2개 생성 완료");
    }

    /**
     * 시간 간격 테스트 데이터 생성 (미래 시간 포함)
     */
    public void generateScheduledTestData() {
        log.info("📝 시간 간격 테스트 데이터 생성 중...");

        LocalDateTime now = LocalDateTime.now();

        // 1. 즉시 처리될 알림 (과거 시간)
        NotificationRequest immediateRequest = NotificationRequest.builder()
            .title("즉시 처리 알림")
            .content("이 알림은 바로 처리되어야 합니다.")
            .subscriberType(SubscriberType.ALL)
            .notificationType(NotificationType.SYSTEM)
            .publisherType(PublisherType.SYSTEM)
            .createdAt(now.minusMinutes(1)) // 1분 전
            .build();

        redisNotificationQueueService.addNotificationRequest(immediateRequest);

        // 2. 1분 후 처리될 알림
        NotificationRequest delayedRequest = NotificationRequest.builder()
            .title("1분 후 처리 알림")
            .content("이 알림은 1분 후에 처리됩니다.")
            .subscriberType(SubscriberType.CUSTOMER)
            .notificationType(NotificationType.EVENT)
            .publisherType(PublisherType.ADMIN)
            .createdAt(now.plusMinutes(1)) // 1분 후
            .build();

        redisNotificationQueueService.addNotificationRequest(delayedRequest);

        log.info("✅ 시간 간격 테스트 데이터 2개 생성 완료");
    }

    /**
     * 현재 큐 상태 출력
     */
    public void printQueueStatus() {
        long queueSize = redisNotificationQueueService.getQueueSize();
        log.info("📊 현재 Redis 큐 상태: {}개 알림 대기 중", queueSize);

        if (queueSize > 0) {
            log.info("🔄 10초 후부터 스케줄러가 처리를 시작합니다.");
            log.info("📈 실시간 모니터링: watch -n 2 'redis-cli ZCARD notification:queue'");
        }
    }
}
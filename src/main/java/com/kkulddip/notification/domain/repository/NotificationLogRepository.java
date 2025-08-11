package com.kkulddip.notification.domain.repository;

import com.kkulddip.notification.domain.model.entity.NotificationLog;
import com.kkulddip.notification.domain.model.enums.RecipientType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 알림 로그 도메인 레포지토리 인터페이스
 *
 * @author Claude
 * @since 1.0
 */
public interface NotificationLogRepository {

    /**
     * 알림 로그를 저장합니다.
     *
     * @param notificationLog 저장할 알림 로그
     * @return 저장된 알림 로그
     */
    NotificationLog save(NotificationLog notificationLog);

    /**
     * ID로 알림 로그를 조회합니다.
     *
     * @param notificationLogId 알림 로그 ID
     * @return 알림 로그 정보
     */
    Optional<NotificationLog> findById(Long notificationLogId);

    /**
     * 알림 ID로 관련된 모든 로그를 조회합니다.
     *
     * @param notificationId 알림 ID
     * @return 해당 알림의 모든 로그
     */
    List<NotificationLog> findByNotificationId(Long notificationId);

    /**
     * 수신자별 알림 로그를 조회합니다.
     *
     * @param recipientUserId 수신자 사용자 ID
     * @param recipientType 수신자 타입
     * @return 해당 수신자의 알림 로그
     */
    List<NotificationLog> findByRecipientUserIdAndRecipientType(Long recipientUserId, RecipientType recipientType);

    /**
     * 발송 실패한 알림 로그 중 재시도 가능한 것들을 조회합니다.
     *
     * @param maxRetryCount 최대 재시도 횟수
     * @return 재시도 가능한 실패 로그 목록
     */
    List<NotificationLog> findFailedLogsForRetry(int maxRetryCount);

    /**
     * 특정 알림의 발송 성공률을 계산하기 위한 통계를 조회합니다.
     *
     * @param notificationId 알림 ID
     * @return 해당 알림의 총 발송 시도 건수와 성공 건수
     */
    NotificationLogStats getStatsByNotificationId(Long notificationId);

    /**
     * 특정 기간 동안의 알림 로그를 조회합니다.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 기간의 알림 로그
     */
    List<NotificationLog> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 특정 사용자의 특정 기간 알림 로그를 조회합니다.
     *
     * @param recipientUserId 수신자 사용자 ID
     * @param recipientType 수신자 타입
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 해당 사용자의 특정 기간 알림 로그
     */
    List<NotificationLog> findByRecipientUserIdAndRecipientTypeAndCreatedAtBetween(
            Long recipientUserId,
            RecipientType recipientType,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    /**
     * 알림 로그 통계를 위한 내부 클래스
     */
    interface NotificationLogStats {
        Long getTotalCount();
        Long getSuccessCount();
        
        default double getSuccessRate() {
            if (getTotalCount() == 0) return 0.0;
            return (double) getSuccessCount() / getTotalCount() * 100;
        }
    }
}
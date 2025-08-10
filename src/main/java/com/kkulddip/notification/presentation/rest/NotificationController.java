package com.kkulddip.notification.presentation.rest;

import com.kkulddip.notification.infrastructure.persistence.redis.RedisNotificationQueueService;
import com.kkulddip.notification.presentation.dto.request.NotificationRequest;
import com.kkulddip.notification.presentation.dto.response.NotificationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final RedisNotificationQueueService redisNotificationQueueService;

    /**
     * 알림 발송 요청 (Redis에 저장)
     */
    @PostMapping("/send")
    public ResponseEntity<NotificationResponse> sendNotification(
            @Valid @RequestBody NotificationRequest request) {
        
        try {
            log.info("알림 발송 요청 수신: {}", request.getTitle());
            
            // 요청에 ID와 생성시간 설정
            if (request.getId() == null) {
                request.setId(UUID.randomUUID().toString());
            }
            if (request.getCreatedAt() == null) {
                request.setCreatedAt(LocalDateTime.now());
            }
            
            // Redis ZSet에 알림 요청 저장
            redisNotificationQueueService.addNotificationRequest(request);
            
            // 현재 큐 크기 확인
            long queueSize = redisNotificationQueueService.getQueueSize();
            
            log.info("알림 요청이 Redis 큐에 저장되었습니다. ID: {}, 현재 큐 크기: {}", 
                    request.getId(), queueSize);
            
            NotificationResponse response = NotificationResponse.builder()
                    .success(true)
                    .message("알림 요청이 성공적으로 등록되었습니다.")
                    .notificationId(request.getId())
                    .queueSize(queueSize)
                    .timestamp(LocalDateTime.now())
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("알림 발송 요청 처리 중 오류 발생: {}", e.getMessage(), e);
            
            NotificationResponse response = NotificationResponse.builder()
                    .success(false)
                    .message("알림 요청 처리 중 오류가 발생했습니다: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
    /**
     * Redis 큐 상태 조회
     */
    @GetMapping("/queue/status")
    public ResponseEntity<?> getQueueStatus() {
        try {
            long queueSize = redisNotificationQueueService.getQueueSize();
            
            return ResponseEntity.ok()
                    .body(java.util.Map.of(
                            "queueSize", queueSize,
                            "timestamp", LocalDateTime.now(),
                            "status", "healthy"
                    ));
                    
        } catch (Exception e) {
            log.error("큐 상태 조회 중 오류 발생: {}", e.getMessage());
            
            return ResponseEntity.internalServerError()
                    .body(java.util.Map.of(
                            "error", e.getMessage(),
                            "timestamp", LocalDateTime.now(),
                            "status", "error"
                    ));
        }
    }
    
    /**
     * 개발용: Redis 큐 내용 로그 출력
     */
    @GetMapping("/queue/debug")
    public ResponseEntity<String> debugQueue() {
        try {
            redisNotificationQueueService.logQueueContents();
            return ResponseEntity.ok("큐 내용이 로그에 출력되었습니다.");
        } catch (Exception e) {
            log.error("큐 디버그 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("큐 디버그 중 오류 발생: " + e.getMessage());
        }
    }
}
package com.kkulddip.notification.interfaces.event.testUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API로 테스트 데이터 생성을 제어하는 컨트롤러
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Profile("develop")
@Slf4j
class TestDataController {

    private final RedisTestDataGenerator testDataGenerator;

    @PostMapping("/redis/basic")
    public ResponseEntity<String> generateBasicTestData() {
        try {
            testDataGenerator.clearQueue();
            testDataGenerator.generateBasicTestData();
            testDataGenerator.printQueueStatus();
            return ResponseEntity.ok("✅ 기본 테스트 데이터 5개 생성 완료");
        } catch (Exception e) {
            log.error("테스트 데이터 생성 실패", e);
            return ResponseEntity.status(500).body("❌ 테스트 데이터 생성 실패: " + e.getMessage());
        }
    }

    @PostMapping("/redis/mass/{count}")
    public ResponseEntity<String> generateMassTestData(@PathVariable int count) {
        try {
            if (count > 1000) {
                return ResponseEntity.badRequest().body("❌ 최대 1000개까지만 생성 가능합니다.");
            }

            testDataGenerator.clearQueue();
            testDataGenerator.generateMassTestData(count);
            testDataGenerator.printQueueStatus();
            return ResponseEntity.ok("✅ 대용량 테스트 데이터 " + count + "개 생성 완료");
        } catch (Exception e) {
            log.error("대용량 테스트 데이터 생성 실패", e);
            return ResponseEntity.status(500).body("❌ 대용량 테스트 데이터 생성 실패: " + e.getMessage());
        }
    }

    @PostMapping("/redis/error")
    public ResponseEntity<String> generateErrorTestData() {
        try {
            testDataGenerator.generateErrorTestData();
            testDataGenerator.printQueueStatus();
            return ResponseEntity.ok("✅ 오류 상황 테스트 데이터 생성 완료");
        } catch (Exception e) {
            log.error("오류 테스트 데이터 생성 실패", e);
            return ResponseEntity.status(500).body("❌ 오류 테스트 데이터 생성 실패: " + e.getMessage());
        }
    }

    @PostMapping("/redis/scheduled")
    public ResponseEntity<String> generateScheduledTestData() {
        try {
            testDataGenerator.generateScheduledTestData();
            testDataGenerator.printQueueStatus();
            return ResponseEntity.ok("✅ 시간 간격 테스트 데이터 생성 완료");
        } catch (Exception e) {
            log.error("시간 간격 테스트 데이터 생성 실패", e);
            return ResponseEntity.status(500).body("❌ 시간 간격 테스트 데이터 생성 실패: " + e.getMessage());
        }
    }

    @PostMapping("/redis/clear")
    public ResponseEntity<String> clearQueue() {
        try {
            testDataGenerator.clearQueue();
            return ResponseEntity.ok("✅ Redis 큐 정리 완료");
        } catch (Exception e) {
            log.error("큐 정리 실패", e);
            return ResponseEntity.status(500).body("❌ 큐 정리 실패: " + e.getMessage());
        }
    }

    @GetMapping("/redis/status")
    public ResponseEntity<String> getQueueStatus() {
        long queueSize = testDataGenerator.redisNotificationQueueService.getQueueSize();
        return ResponseEntity.ok("📊 현재 Redis 큐 상태: " + queueSize + "개 알림 대기 중");
    }
}
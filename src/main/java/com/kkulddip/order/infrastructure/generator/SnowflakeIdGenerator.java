package com.kkulddip.order.infrastructure.generator;

import org.springframework.stereotype.Component;

/**
 * Twitter Snowflake 알고리즘을 구현한 ID 생성기
 * 64비트 ID를 생성하며, 다음 구조를 가진다:
 * - 1비트: 사용하지 않음 (항상 0)
 * - 41비트: 타임스탬프 (밀리초 단위, epoch from 2024-01-01)
 * - 10비트: 워커 ID (0-1023)
 * - 12비트: 시퀀스 번호 (0-4095)
 */
@Component
public class SnowflakeIdGenerator {
    
    // 2024-01-01 00:00:00 UTC를 기준 epoch로 사용
    private static final long EPOCH = 1704067200000L; // 2024-01-01 00:00:00 UTC
    
    private static final long WORKER_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;
    
    private static final long MAX_WORKER_ID = (1L << WORKER_ID_BITS) - 1; // 1023
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1; // 4095
    
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS; // 12
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS; // 22
    
    private final long workerId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;
    
    public SnowflakeIdGenerator() {
        // 단일 인스턴스 환경에서는 고정 워커 ID 사용 (0)
        // 분산 환경에서는 설정으로 주입하거나 동적 할당 가능
        this.workerId = 0L;
        
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("Worker ID는 0부터 " + MAX_WORKER_ID + " 사이여야 합니다");
        }
    }
    
    /**
     * 새로운 Snowflake ID 생성
     */
    public synchronized long generate() {
        long timestamp = System.currentTimeMillis();
        
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("시계가 뒤로 갔습니다. ID 생성을 거부합니다");
        }
        
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 같은 밀리초 내에서 시퀀스가 최대치에 도달하면 다음 밀리초까지 대기
                timestamp = waitForNextMillisecond(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        
        lastTimestamp = timestamp;
        
        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
            | (workerId << WORKER_ID_SHIFT)
            | sequence;
    }
    
    private long waitForNextMillisecond(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
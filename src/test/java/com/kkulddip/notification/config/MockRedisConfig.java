package com.kkulddip.notification.config;

import com.kkulddip.notification.infrastructure.persistence.redis.RedisNotificationQueueService;
import com.kkulddip.notification.presentation.dto.request.NotificationRequest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@TestConfiguration
@Profile("citest")
public class MockRedisConfig {

    @Bean
    @Primary
    public RedisNotificationQueueService mockRedisNotificationQueueService() {
        return new MockRedisNotificationQueueService();
    }

    public static class MockRedisNotificationQueueService extends RedisNotificationQueueService {

        private final Set<NotificationRequest> mockQueue = new ConcurrentSkipListSet<>(
            (a, b) -> Long.compare(a.getScoreTimestamp(), b.getScoreTimestamp())
        );

        public MockRedisNotificationQueueService() {
            super(null, null);
        }

        @Override
        public Set<NotificationRequest> popNotificationRequests(int count) {
            Set<NotificationRequest> result = ConcurrentHashMap.newKeySet();

            int taken = 0;
            var iterator = mockQueue.iterator();
            while (iterator.hasNext() && taken < count) {
                NotificationRequest request = iterator.next();
                result.add(request);
                iterator.remove();
                taken++;
            }

            return result;
        }

        @Override
        public long getQueueSize() {
            return mockQueue.size();
        }

        @Override
        public void addNotificationRequest(NotificationRequest request) {
            mockQueue.add(request);
        }

        @Override
        public void logQueueContents() {
            System.out.println("Mock Queue Size: " + mockQueue.size());
        }
    }
}
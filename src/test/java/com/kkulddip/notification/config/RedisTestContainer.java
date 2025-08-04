package com.kkulddip.notification.config;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Redis TestContainer 헬퍼 클래스
 */
public class RedisTestContainer {

    private static final String REDIS_IMAGE = "redis:7-alpine";
    private static final int REDIS_PORT = 6379;

    public static GenericContainer<?> createRedisContainer() {
        return new GenericContainer<>(DockerImageName.parse(REDIS_IMAGE))
            .withExposedPorts(REDIS_PORT)
            .withReuse(true); // 컨테이너 재사용으로 테스트 속도 향상
    }
}
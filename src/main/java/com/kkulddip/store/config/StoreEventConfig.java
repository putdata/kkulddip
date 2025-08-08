package com.kkulddip.store.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Store 도메인 이벤트 처리를 위한 비동기 설정
 */
@Slf4j
@EnableAsync
@Configuration
public class StoreEventConfig {
    
    /**
     * Store 이벤트 처리용 스레드 풀
     */
    @Bean("storeEventTaskExecutor")
    public TaskExecutor storeEventTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 코어 스레드 수
        executor.setCorePoolSize(2);
        
        // 최대 스레드 수
        executor.setMaxPoolSize(5);
        
        // 큐 용량
        executor.setQueueCapacity(25);
        
        // 스레드 이름 prefix
        executor.setThreadNamePrefix("store-event-");
        
        // 스레드 풀 종료 시 대기 시간
        executor.setAwaitTerminationSeconds(20);
        
        // 애플리케이션 종료 시 남은 작업 완료 대기
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 거절 정책: 호출자 스레드에서 실행
        executor.setRejectedExecutionHandler((r, executor1) -> {
            log.warn("Store 이벤트 처리 스레드 풀이 포화 상태입니다. 호출자 스레드에서 실행합니다.");
            r.run();
        });
        
        executor.initialize();
        
        log.info("Store 이벤트 처리용 스레드 풀이 초기화되었습니다. " +
                "CorePoolSize: {}, MaxPoolSize: {}, QueueCapacity: {}", 
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }
}
package com.kkulddip.payment.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class SchedulingConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
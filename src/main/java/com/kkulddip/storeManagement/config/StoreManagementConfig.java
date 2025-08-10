package com.kkulddip.storeManagement.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 가게 관리 도메인 설정 클래스
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "com.kkulddip.storeManagement")
public class StoreManagementConfig {
    
    // 필요시 추가 Bean 정의
}
package com.kkulddip.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("KKULDDIP API")
                .version("1.0.0")
                .description("KKULDDIP 프로젝트 API 문서입니다.")
                .contact(new Contact()
                    .name("KKULDDIP Team")
                    .email("contact@kkulddip.com"))
            )
            .servers(List.of(
                new Server().url("http://localhost:8080").description("개발 서버")
            ));
    }

    @Bean
    GroupedOpenApi notificationOpenApi() {
        String[] paths = {"/api/notification/**"};
        return GroupedOpenApi.builder()
                .group("Notification 관련 API")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi userTokenApi() {
        String[] paths = {"/api/tokens/**"};
        return GroupedOpenApi.builder()
                .group("토큰 관련 API")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi statisticsApi() {
        String[] paths = {"/api/statistics/**"};
        return GroupedOpenApi.builder()
                .group("통계 관련 API")
                .pathsToMatch(paths)
                .build();
    }
} 
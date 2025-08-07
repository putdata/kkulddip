package com.kkulddip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableJpaAuditing
@EnableFeignClients
public class KkulddipApplication {

	public static void main(String[] args) {
		SpringApplication.run(KkulddipApplication.class, args);
	}

}

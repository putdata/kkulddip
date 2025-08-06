package com.kkulddip;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("citest")
// @Import(TestConfig.class)
class KkulddipApplicationTests {

	@Test
	void contextLoads() {
	}

}

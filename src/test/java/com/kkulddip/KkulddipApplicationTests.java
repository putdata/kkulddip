package com.kkulddip;

import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("citest")
// @Import(TestConfig.class)
class KkulddipApplicationTests {

	@MockitoBean
	private FirebaseMessaging firebaseMessaging;
	
	@Test
	void contextLoads() {
	}

}

package com.example.paymentContainer;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// THÊM classes attribute để chỉ định configuration class
@SpringBootTest(classes = PaymentContainerApplicationTests.TestConfig.class)
class PaymentContainerApplicationTests {

	@Test
	void contextLoads() {
		// Test sẽ pass nếu Spring context load thành công
	}

	// TẠO inner configuration class cho test
	@org.springframework.boot.test.context.TestConfiguration
	static class TestConfig {
		// Có thể thêm @Bean definitions nếu cần
		// @Bean
		// public SomeService someService() {
		//     return new SomeService();
		// }
	}
}
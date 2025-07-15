package com.example.druguseprevention;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties") // dùng riêng cấu hình test
class DrugUsePreventionApplicationTests {
	@Test
	void contextLoads() {
	}
}

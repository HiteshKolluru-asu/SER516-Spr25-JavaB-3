package com.defectdensityapi;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DefectDensityApiApplicationTests {

	@Test
	void contextLoads() {
		assertDoesNotThrow(() -> DefectDensityApiApplication.main(new String[]{}));
	}

}

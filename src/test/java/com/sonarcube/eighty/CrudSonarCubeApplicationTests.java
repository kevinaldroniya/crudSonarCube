package com.sonarcube.eighty;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class CrudSonarCubeApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainMethodShouldRun(){
		CrudSonarCubeApplication.main(new String[] {});
		assertNotNull(CrudSonarCubeApplication.class);
	}

}

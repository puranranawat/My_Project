package com.devsecops.ems;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for the EMS application context.
 *
 * <p>This test verifies that the Spring application context loads successfully.
 * It uses the "test" profile which configures an H2 in-memory database
 * (see application-test.properties).</p>
 *
 * <p>To run this test, ensure the H2 dependency is available on the test classpath:
 * <pre>{@code
 * <dependency>
 *     <groupId>com.h2database</groupId>
 *     <artifactId>h2</artifactId>
 *     <scope>test</scope>
 * </dependency>
 * }</pre></p>
 */
@SpringBootTest
@ActiveProfiles("test")
class EmsApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that the Spring application context starts up without errors
    }
}

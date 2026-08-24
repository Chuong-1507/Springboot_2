package com.example.springboot_2.security;

import com.example.springboot_2.security.integration.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"integration", "extra-profile"})  // gộp thêm, không thay thế
class SpringBoot2ApplicationTests extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
    }

}

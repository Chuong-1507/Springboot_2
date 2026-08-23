package com.example.springboot_2.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers // cho phép JUnit tự quản lý vòng đời container (start trước test, stop sau khi hết class test)
public class AuthIntegrationTestcontainersTest {
    @Container// Đánh dâú một container
    @ServiceConnection// Tự động bind thông tin kết nối của container vào DataSource
    static MySQLContainer mySQLContainer = new MySQLContainer("mysql:8.0");

    @Autowired
    private MockMvc mockMvc; // MockMvc gọi các API (request tới endpoint thật)

    @Test
    void contextLoads(){
        // Test rỗng để xác nhận Spring context load được với container thật
    }
}


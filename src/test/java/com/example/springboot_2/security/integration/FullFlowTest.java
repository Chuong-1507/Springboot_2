package com.example.springboot_2.security.integration;

import com.example.springboot_2.controller.TestProtectedController;
import com.example.springboot_2.model.User.User;
import com.example.springboot_2.repository.User.RefreshTokenRepository;
import com.example.springboot_2.repository.User.UserRepository;
import com.example.springboot_2.security.dto.LoginRequest;

import org.springframework.context.annotation.Import;
import tools.jackson.databind.ObjectMapper;   // Jackson 3.x
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest// cho phép chạy toàn bộ Spring Application Context để test
@AutoConfigureMockMvc // cho phép bạn giả lập HTTP request mà không cần mở trình duyệt
@Import(TestProtectedController.class)
class AuthFullFlowTestContainerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper; //Object Mapper dùng để chuyển Java Object -> JSON

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();   // xóa con trước

        userRepository.deleteAll();
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("correctPassword"));
        userRepository.save(user);
    }

    @Test
    void fullFlow_loginThenAccessProtected_shouldSucceed() throws Exception{
        var loginRequest = new LoginRequest("testuser","correctPassword");

        String responseBody = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = objectMapper.readTree(responseBody).get("accessToken").asString();

        mockMvc.perform(get("/api/test/protected")
                .header("Authorization","Bearer "+accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    void login_wrongPassword_shouldReturn401() throws Exception {
        var request = new LoginRequest("testuser", "wrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_usernameNotExist_shouldReturn401() throws Exception {
        var request = new LoginRequest("ghostUser", "anyPassword");

       mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @ParameterizedTest//Cho phép bạn chạy cùng một test nhiều lần với nhiều kiểu dữ liệu khác nhau
    @CsvSource({
            "testuser, ''",
            "'', correctPassword",
            "'', ''",
            "fskdjf,gg"
    })
    void login_emptyCredentials_shouldReturn401OrBadRequest(String username, String password) throws Exception {
        var request = new LoginRequest(username, password);

       mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());// 400 hoặc 401 tùy validation
    }
}
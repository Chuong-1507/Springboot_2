package com.example.springboot_2.controller;

import com.example.springboot_2.model.User.RefreshToken;
import com.example.springboot_2.model.User.Role;
import com.example.springboot_2.model.User.User;
import com.example.springboot_2.repository.User.UserRepository;
import com.example.springboot_2.security.AuthController;
import com.example.springboot_2.security.JwtService;
import com.example.springboot_2.security.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(controllers = AuthController.class,
            excludeAutoConfiguration = {
            OAuth2ClientAutoConfiguration .class,
            OAuth2ClientWebSecurityAutoConfiguration .class
        })
public class AuthControllerWebMvcTest {
    @Autowired
    private MockMvc mockMvc;

    //Boot 4: dùng @Mockito thay cho @MockBean (đã bị xóa ở 4.0.0)
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private PasswordEncoder passwordEncoder;
    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private UserDetailsService userDetailsService;
    /**
     * Test này KHÔNG kiểm tra logic xác thực thật (password không được so sánh vì
     * authenticationManager đã bị @MockitoBean - mọi password đều "pass" âm thầm).
     *
     * Mục đích thực sự: kiểm tra "đường ống" (wiring) của endpoint /api/auth/login -
     * tức là khi các dependency (userRepository, jwtService, refreshTokenService) hoạt
     * động đúng như kỳ vọng, Controller có:
     *   1. Trả về đúng HTTP status 200
     *   2. Trả về đúng cấu trúc JSON (accessToken, refreshToken) với đúng giá trị
     *      mà các bean mock đã được khai báo trả về
     *
     * Lưu ý: username "testuser" chỉ có tác dụng vì nó khớp CHÍNH XÁC với chuỗi
     * đã stub trong userRepository.findByUsername("testuser") - đây không phải
     * là bằng chứng cho việc tra cứu DB thật hoạt động đúng.
     *
     * Để test logic sai password/sai username thật, cần viết test riêng có
     * when(authenticationManager.authenticate(any())).thenThrow(...).
     */
    @Test
    void login_withValidCredential_shouldReturn200AndTokens() throws Exception{
        User fakeUser = new User();
        fakeUser.setUsername("testuser");
        fakeUser.setRoles(Set.of(Role.USER));

        RefreshToken fakeRefreshToken = new RefreshToken();
        fakeRefreshToken.setToken("fake-refresh-token-456");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(jwtService.generateToken(fakeUser)).thenReturn("fake-jwt-token-123");
        when(refreshTokenService.createRefreshToken(fakeUser)).thenReturn(fakeRefreshToken);

        String requestBody = """
                {
                    "username": "testuser",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("fake-jwt-token-123"))
                .andExpect(jsonPath("$.refreshToken").value("fake-refresh-token-456"));
    }
}

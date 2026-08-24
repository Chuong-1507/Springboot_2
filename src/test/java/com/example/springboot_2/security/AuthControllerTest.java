package com.example.springboot_2.security;

import com.example.springboot_2.model.User.RefreshToken;
import com.example.springboot_2.model.User.User;
import com.example.springboot_2.repository.User.UserRepository;
import com.example.springboot_2.security.dto.AuthResponse;
import com.example.springboot_2.security.dto.LoginRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AuthControllerTest {
    private UserRepository mockUserRepository;
    private PasswordEncoder mockPasswordEncoder;
    private AuthenticationManager mockAuthManager;
    private JwtService mockJwtService;
    private RefreshTokenService mockRefreshTokenService;


    private AuthController authController;


    @BeforeEach
    void setUp(){
        //tạo mock cho mọi dependency, không dùng bean thật nào
        mockJwtService = mock(JwtService.class);
        mockAuthManager = mock(AuthenticationManager.class);
        mockUserRepository = mock(UserRepository.class);
        mockRefreshTokenService = mock(RefreshTokenService.class);
        mockPasswordEncoder = mock(PasswordEncoder.class);
        authController = new AuthController(
                mockUserRepository,
                mockPasswordEncoder,
                mockAuthManager,
                mockJwtService,
                mockRefreshTokenService
        );
    }

    @Test
    void login_withValidCredential_shouldReturnAuthResponseWithFakeTokens(){
        User fakeUser = new User();
        fakeUser.setUsername("testuser");

        RefreshToken fakeRefreshToken = new RefreshToken();
        fakeRefreshToken.setToken("fake-refresh-token-456");

        when(mockUserRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));
        when(mockJwtService.generateToken(fakeUser)).thenReturn("fake-jwt-123");
        when(mockRefreshTokenService.createRefreshToken(fakeUser)).thenReturn(fakeRefreshToken);

        LoginRequest request = new LoginRequest("testuser","123456");

        ResponseEntity<?> response = authController.login(request);

        AuthResponse body = (AuthResponse) response.getBody();
        assertThat(body.getAccessToken()).isEqualTo("fake-jwt-123");
        assertThat(body.getRefreshToken()).isEqualTo("fake-refresh-token-456");

        //Xác nhận authenticate() thực sự được gọi ( bước bắt buộc )
        verify(mockAuthManager,times(1)).authenticate(any());
    }
}

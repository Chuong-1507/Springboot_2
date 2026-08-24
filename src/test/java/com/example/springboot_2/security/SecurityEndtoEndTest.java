package com.example.springboot_2.security;

import com.example.springboot_2.controller.TestProtectedController;
import com.example.springboot_2.model.User.Role;
import com.example.springboot_2.model.User.User;
import com.example.springboot_2.repository.User.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestProtectedController.class)
class SecurityEndToEndTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtService jwtService; // dùng THẬT, không mock, để sinh token thật

    @MockitoBean
    private UserRepository userRepository; // chỉ mock tầng DB

    private MockMvc mockMvc;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        // build MockMvc từ context thật, có đầy đủ Security filter chain
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    /**Nếu Client gọi API được bảo vệ nhưng không gửi JWT thì Spring Security không chặn lại
     * -> Trả $)! Unauthorized
     */
    @Test
    void accessProtectedEndpoint_withoutToken_shouldReturn401() throws  Exception{
        mockMvc.perform(get("/api/some-protected-endpoint"))
                .andExpect(status().isUnauthorized());
    }
    /**Nếu User có JWT hợp lệ thì có thể truy cập API được bảo vệ
     */
    @Test
    void accessProtectedEndpoint_withValidToken_shouldReturn200() throws Exception{
        //Tạo User giả
        User fakeUser = new User();
        fakeUser.setUsername("testuser");
        fakeUser.setRoles(Set.of(Role.USER));

        //Mock UserRepository
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(fakeUser));

        //Sinh token thật bằng jwtService thật -> đúng chữ ký, đúng hạn
        String realToken = jwtService.generateToken(fakeUser);
        //Gửi JWT đó vào request như client thật
        mockMvc.perform(get("/api/test/protected")
                .header("Authorization","Bearer "+ realToken))
                .andExpect(status().isOk());

    }

}
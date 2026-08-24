package com.example.springboot_2.exception;

import com.example.springboot_2.model.User.Role;
import com.example.springboot_2.model.User.User;
import com.example.springboot_2.repository.User.UserRepository;
import com.example.springboot_2.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException{
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name  = oAuth2User.getAttribute("name");

        //Tìm user theo email, nếu chưa có thì tự tạo mới (không cần password)
        User user = userRepository.findByUsername(email)
                .orElseGet(()->{
                    User newUser = new User();
                    newUser.setUsername(email);
                    newUser.setPassword(""); // Không cần password
                    newUser.setRoles(Collections.singleton(Role.USER));
                    return userRepository.save(newUser);
                });

        //Tạo JWT Token
        String token = jwtService.generateToken(user);

        response.sendRedirect("http://localhost:3000/oauth-success?token=" + token);
    }
}

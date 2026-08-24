package com.example.springboot_2.security;

import com.example.springboot_2.model.User.RefreshToken;
import com.example.springboot_2.security.dto.AuthResponse;
import com.example.springboot_2.security.dto.RefreshTokenRequest;
import com.example.springboot_2.security.dto.RegisterRequest;
import com.example.springboot_2.model.User.Role;
import com.example.springboot_2.model.User.User;
import com.example.springboot_2.repository.User.UserRepository;
import com.example.springboot_2.security.dto.LoginRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthController {
        UserRepository userRepository;
        PasswordEncoder passwordEncoder;
        AuthenticationManager authenticationManager;
        JwtService jwtService;
        RefreshTokenService refreshTokenService;



        //Đăng kí tài khoản User
        @PostMapping("/register")
        public ResponseEntity<?> register(@RequestBody RegisterRequest request){
                if (userRepository.findByUsername(request.getUsername()).isPresent())
                        return ResponseEntity.badRequest().body("Username đã tồn tại ");

                User user = new User();
                user.setUsername(request.getUsername());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setRoles(Set.of(Role.USER));

                userRepository.save(user);
                return ResponseEntity.ok("Đăng kí tài khoản thành công");
        }



        //Đăng nhập tài khoản User
        @PostMapping("/login")
        public ResponseEntity<?> login(@RequestBody LoginRequest request){
                //Xác thực usermame/password - nếu sai sẽ tự ném Exception
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword())
                );

                User user = userRepository.findByUsername(request.getUsername())
                        .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy user"));

                String accessToken = jwtService.generateToken(user);
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

                return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken()));
        }

        //Refresh lại token
        @PostMapping("/refresh")
        public ResponseEntity<?> refresh(@RequestBody RefreshTokenRequest request){
                RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken())
                        .orElseThrow(()-> new RuntimeException("Refresh token invalid "));

                refreshTokenService.verifyExpiration(refreshToken); // ném exception khi hết hạn

                User user = refreshToken.getUser();
                String newAccessToken = jwtService.generateToken(user);

                //access token mới, refresh token giữ nguyên (không bắt buộc phải cập nhật mới)
                return ResponseEntity.ok(new AuthResponse(newAccessToken, refreshToken.getToken()));
        }

}

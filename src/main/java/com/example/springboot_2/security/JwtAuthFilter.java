package com.example.springboot_2.security;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;   // inject thêm

    /**Spring sẽ tự động gọi hàm này mỗi khi có HTTP request đi qua Securitỳ Filter Chain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("DEBUG: Không có header hoặc sai format Bearer");
            filterChain.doFilter(request, response);// chuyển tiếp sang filter tiếp theo
            return;
        }

        final String token = authHeader.substring(7);
        System.out.println("DEBUG token = [" + token + "]");

        if (jwtService.isTokenValid(token)) {
            System.out.println("DEBUG: Token VALID");

            String username = jwtService.extractUsername(token);

            // chỉ set nếu context chưa có authentication (tránh override)
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);// Lấy userDetails trong DB

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()   // constructor 3 tham số — bắt buộc
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Tạo authentication details từ HTTP request, nó chỉ tồn tại cho đến khi request kết thúc

                SecurityContextHolder.getContext().setAuthentication(authToken);
                // Đặt Authentication của request hiện tại vào SecurityContext
                // để các thành phần phía sau có thể lấy thông tin user và quyền
                System.out.println("DEBUG: Đã set authentication cho user " + username);
            }

        } else {
            System.out.println("DEBUG: Token INVALID");
        }

        filterChain.doFilter(request, response);
    }
}
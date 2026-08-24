package com.example.springboot_2.security;

import com.example.springboot_2.model.User.Role;
import com.example.springboot_2.repository.User.UserRepository;
import com.example.springboot_2.model.User.User;   // import entity User (hay dùng hơn trong file này)
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


/* Nhiệm vụ chính: dịch entity User (DB) sang UserDetails (Spring Security hiểu)
- Đuợc Spring Security tự động gọi mỗi khi cần xác thực (Lúc login, validate JWT)
- Nhận username, trả về UserDetails (username, password đã hash, authorities)
 lấy từ UserRepository
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)   // giờ User = entity của bạn, ngắn gọn
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy User: " + username));

        String[] roleNames = user.getRoles().stream()
                .map(Enum::name)
                .toArray(String[]::new);

        return org.springframework.security.core.userdetails.User.builder()   // Spring Security User → viết full path
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(roleNames)
                .build();
    }
}
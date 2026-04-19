package com.example.backend_tmdt.security;

import com.example.backend_tmdt.entity.UserEntity;
import com.example.backend_tmdt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String loginKey) throws UsernameNotFoundException {
        // Tìm user bằng email, phone hoặc username
        UserEntity user = userRepository.findByLoginKey(loginKey)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản với thông tin: " + loginKey));

        // Lấy danh sách quyền (Role) và chuyển sang GrantedAuthority của Spring Security
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());

        // Trả về đối tượng User chuẩn của Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}

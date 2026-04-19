package com.example.backend_tmdt.security;

import com.example.backend_tmdt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Lấy token từ Header Authorization
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmailOrUsername;

        // 2. Nếu không có token hoặc không bắt đầu bằng "Bearer " -> bỏ qua
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Cắt bỏ "Bearer " lấy chuỗi token
        jwt = authHeader.substring(7);

        try {
            // 4. Giải mã token lấy username
            userEmailOrUsername = jwtService.extractUsername(jwt);
        } catch (Exception ex) {
            // Token sai định dạng hoặc hết hạn -> bỏ qua
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Nếu có username và chưa được xác thực trong SecurityContext
        if (userEmailOrUsername != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmailOrUsername);

            // 6. Kiểm tra token hợp lệ
            if (jwtService.isTokenValid(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. Lưu vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/") || HttpMethod.OPTIONS.matches(request.getMethod());
    }
}

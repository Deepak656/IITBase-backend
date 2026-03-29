package com.iitbase.config;

import com.iitbase.auth.TokenService;
import com.iitbase.user.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // Step 1: Validate JWT structure and expiration
            if (!jwtUtil.isTokenValid(token)) {
                log.warn("Invalid or expired JWT token");
                filterChain.doFilter(request, response);
                return;
            }

            // Step 2: Extract JTI and validate against Redis whitelist
            String jti = jwtUtil.extractJti(token);
            if (jti == null || !tokenService.isTokenValid(jti)) {
                log.warn("Token not in Redis whitelist - JTI: {}", jti);
                filterChain.doFilter(request, response);
                return;
            }

            // Step 3: Authenticate user
            String email = jwtUtil.extractEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Store JTI in request for logout
                request.setAttribute("jti", jti);

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.debug("User authenticated: {}", email);
            }

        } catch (Exception e) {
            log.error("JWT authentication failed", e);
        }

        filterChain.doFilter(request, response);
    }
}
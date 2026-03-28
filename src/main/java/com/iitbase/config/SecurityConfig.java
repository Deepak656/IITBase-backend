package com.iitbase.config;

import io.netty.handler.codec.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth

                        // 🔓 PUBLIC (no auth required)
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/public/**",
                                "/api/jobs/**",          // job listings should be public
                                "/api/companies/**",      // public company profiles
                                "/api/auth/health/redis" // redis health check
                        ).permitAll()
                        // Admin staff invite and accept public url
                        .requestMatchers("/api/admin/staff/invite/validate").permitAll()
                        .requestMatchers("/api/admin/staff/invite/accept").permitAll()
                        // 👤 AUTHENTICATED USERS (any logged-in user)
                        .requestMatchers(
                                "/api/user/**"
                        ).authenticated()

                        // 🎓 JOB SEEKER
                        .requestMatchers(
                                "/api/applications/**"
                        ).hasRole("JOB_SEEKER")

                        // 🏢 RECRUITER
                        .requestMatchers(
                                "/api/recruiter/**",
                                "/api/v1/recruiters/**",
                                "/api/v1/companies/**"
                        ).hasRole("RECRUITER")

                        // 🛠 ADMIN
                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        // 🔒 Everything else
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

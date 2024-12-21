package com.kosa.moimeasy.security.config;

import com.kosa.moimeasy.security.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors
                        .configurationSource(request -> {
                            CorsConfiguration config = new CorsConfiguration();

                            config.addAllowedOrigin("http://localhost:3000");
                            //config.setAllowedOrigins(List.of("http://localhost:5137")); //Vue.js

                            config.addAllowedOrigin("*");
                            config.setAllowedOrigins(List.of("http://localhost:3000")); //Vue.js

                            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH" , "OPTIONS"));
                            config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Refresh-Token"));
                            config.setAllowCredentials(true); //쿠키 전송 허용
                            return config;
                        })

                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Session off
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/api/v1/login", "/api/v1/signup/**","/api/v1/find/**", "/api/v1/reset/**", "/api/v1/logout/**","/").permitAll() // 로그인 및 회원가입 엔드포인트는 누구나 접근 가능
                                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN") // 'admin'역할만 접근 가능
                                //.requestMatchers("/api/v1/{reviewId}/**").hasRole("OWNER") // 'owner' 역할만 접근 가능
                                //.anyRequest().hasRole("USER") //로그인 한 사용자 'user' 역활 필요
                                .anyRequest().permitAll() //나머지 모든 요청, 허용(개발 중)
                        //.anyRequest().authenticated() //인증된 사용자만 이용

                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

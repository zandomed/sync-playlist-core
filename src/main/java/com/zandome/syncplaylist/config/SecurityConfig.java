package com.zandome.syncplaylist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 🔴 Disable CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // 🔴 Allow all
                )
                .formLogin(form -> form.disable()) // 🔴 Disable login form
                .httpBasic(basic -> basic.disable()); // 🔴 Disable Basic Auth

        return http.build();
    }
}

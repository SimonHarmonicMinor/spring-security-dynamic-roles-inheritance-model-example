package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    @SneakyThrows
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                   .csrf().disable()
                   .cors().disable()
                   .authorizeHttpRequests(customizer -> customizer.anyRequest().authenticated())

                   .httpBasic()
                   .authenticationEntryPoint((request, response, authException) -> response.sendError(401))
                   .and()

                   .build();
    }
}

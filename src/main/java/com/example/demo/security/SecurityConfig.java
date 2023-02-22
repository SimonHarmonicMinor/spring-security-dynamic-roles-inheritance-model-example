package com.example.demo.security;

import com.example.demo.repository.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserRepository userRepository;

    @Bean
    @SneakyThrows
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                   .csrf().disable()
                   .cors().disable()
                   .authorizeHttpRequests(customizer -> customizer.anyRequest().authenticated())

                   .httpBasic()
                   .authenticationEntryPoint((request, response, authException) -> response.sendError(401))
                   .authenticationDetailsSource(new AuthenticationDetailsSource<HttpServletRequest, Object>() {
                       @Override
                       public Object buildDetails(HttpServletRequest context) {
                           throw new UnsupportedOperationException("Not implemented yet");
                       }
                   })

                   .and()

                   .build();
    }

    @Bean
    public AuthenticationProvider dbAuthenticationProvider() {
        return new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                final var password = authentication.getCredentials().toString();
                if (!"password".equals(password)) {
                    throw new AuthenticationServiceException("Invalid username or password");
                }
                return null;
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return UsernamePasswordAuthenticationToken.class.equals(authentication);
            }
        };
    }
}

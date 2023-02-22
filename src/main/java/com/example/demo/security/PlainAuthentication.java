package com.example.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import lombok.RequiredArgsConstructor;

import static java.util.Collections.emptySet;

@RequiredArgsConstructor
public class PlainAuthentication implements Authentication {
    private final Long userId;
    private volatile boolean authenticated = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return emptySet();
    }

    @Override
    public Long getPrincipal() {
        return userId;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        authenticated = isAuthenticated;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getDetails() {
        return null;
    }
}

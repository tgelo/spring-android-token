package com.tgelo.security.xauth;

import org.springframework.security.core.Authentication;

public class TokenAndAuthentication {
    private String token;
    private Authentication authentication;

    TokenAndAuthentication(String token, Authentication authentication) {
        this.token = token;
        this.authentication = authentication;
    }

    public String getToken() {
        return token;
    }

    public Authentication getAuthentication() {
        return authentication;
    }
}

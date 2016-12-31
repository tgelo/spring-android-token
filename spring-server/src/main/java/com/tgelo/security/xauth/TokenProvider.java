package com.tgelo.security.xauth;

import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class TokenProvider {

    @Autowired
    private Cache<String, TokenAndAuthentication> cache;

    public String createToken(String userName) {
        return userName + ":" + computeSignature(userName);
    }

    public String computeSignature(String userName) {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(userName).append(":");
        signatureBuilder.append(UUID.randomUUID().toString());

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm available!");
        }
        return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
    }

    public String getUserNameFromToken(String authToken) {
        if (null == authToken) {
            throw new TokenException("Token is empty");
        }
        String[] parts = authToken.split(":");

        if (parts.length < 1) {
            throw new TokenException("Token has wrong format");
        }

        return parts[0];
    }

    public void store(String token, Authentication authentication) {
        String userName = getUserNameFromToken(token);
        cache.put(userName, new TokenAndAuthentication(token, authentication));
    }

    public boolean validateToken(String token) {
        String userName = getUserNameFromToken(token);
        if (cache.containsKey(userName)) {
            TokenAndAuthentication
                tokenAndAuthentication = cache.get(userName);
            return tokenAndAuthentication.getToken().equals(token);
        }
        return false;
    }

    public UsernamePasswordAuthenticationToken retrieve(String token) {
        String username = getUserNameFromToken(token);
        TokenAndAuthentication
            tokenAndAuthentication = cache.get(username);
        return (UsernamePasswordAuthenticationToken) tokenAndAuthentication.getAuthentication();
    }


}
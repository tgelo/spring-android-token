package com.tgelo.security.xauth;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class TokenProvider {

    private static final Cache
        restApiAuthTokenCache =
        CacheManager.getInstance().getCache("restApiAuthTokenCache");
    public static final int HALF_AN_HOUR_IN_MILLISECONDS = 30 * 60 * 1000;

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

    @Scheduled(fixedRate = HALF_AN_HOUR_IN_MILLISECONDS)
    public void evictExpiredTokens() {
        restApiAuthTokenCache.evictExpiredElements();
    }

    public void store(String token, Authentication authentication) {
        String userName = getUserNameFromToken(token);
        restApiAuthTokenCache
            .put(new Element(userName, new TokenAndAuthentication(token, authentication)));
    }

    public boolean validateToken(String token) {
        String userName = getUserNameFromToken(token);
        if (!restApiAuthTokenCache.isKeyInCache(userName)) {
            return false;
        }
        TokenAndAuthentication
            tokenAndAuthentication =
            (TokenAndAuthentication) restApiAuthTokenCache.get(userName).getObjectValue();
        return tokenAndAuthentication.token.equals(token);
    }

    public UsernamePasswordAuthenticationToken retrieve(String token) {
        String username = getUserNameFromToken(token);
        TokenAndAuthentication
            tokenAndAuthentication =
            (TokenAndAuthentication) restApiAuthTokenCache.get(username)
                .getObjectValue();
        return (UsernamePasswordAuthenticationToken) tokenAndAuthentication.authentication;
    }


    private class TokenAndAuthentication {
        private String token;
        private Authentication authentication;

        TokenAndAuthentication(String token, Authentication authentication) {
            this.token = token;
            this.authentication = authentication;
        }
    }

}
package com.tgelo;

import com.tgelo.security.xauth.TokenAndAuthentication;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class EhcacheConfiguration {

    @Value("${ehcache.time-to-live}")
    Long timeToLive;

    @Bean
    public Cache<String, TokenAndAuthentication> createCache() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
        cacheManager.init();
        Cache<String, TokenAndAuthentication> cache =
            cacheManager.createCache("restApiAuthTokenCache",
                CacheConfigurationBuilder
                    .newCacheConfigurationBuilder(String.class, TokenAndAuthentication.class,
                        ResourcePoolsBuilder.heap(10))
                    .withExpiry(Expirations.timeToLiveExpiration(Duration.of(timeToLive, TimeUnit.SECONDS))).build());
        return cache;
    }
}



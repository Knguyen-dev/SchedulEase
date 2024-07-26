package com.knguyendev.api.config.security;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;

/**
 * Configuration class sets up Redis-based session management for the application. It creates a bean for the necessary
 * Redis connection factory and some session management components.
 *
 * NOTE: For flush, and other settings, you'd put it in the annotation
 */
@Configuration
@EnableRedisIndexedHttpSession
public class RedisConfig extends AbstractHttpSessionApplicationInitializer {

    // Expecting 'RedisProperties' to be injected
    private final RedisProperties redisProperties;
    public RedisConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    /**
     * Creates a 'LettuceConnectionFactory' bean that uses the properties from the 'RedisProperties' instance that this
     * class is given.
     */
    @Bean
    public LettuceConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(this.redisProperties.getHost(), this.redisProperties.getPort());
        return new LettuceConnectionFactory(config);
    }

    /**
     * Method allows for us to place constraints on the amount of sessions that a single user can have. After creating
     * this listener, we'll define the maximum amount of sessions a user can have in our SecurityFilterChain and SecurityConfig
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}

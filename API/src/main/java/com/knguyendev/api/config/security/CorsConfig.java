package com.knguyendev.api.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;


/**
 * Configuration class for setting up and creating 'CorsConfigurationSource' bean, which is responsible
 * setting up the Cors configurations for our REST API.
 *
 * NOTE: This bean won't be used in the direct code that we create, but more so the library
 * code that we use. In our SecurityFilterChain we'll call the cors() method, and that's where
 * this 'CorsConfigurationSource' bean is used. Also the bean is automatically used by Spring Security
 * so we don't need to explicitly reference or inject it anywhere.
 */
@Configuration
public class CorsConfig {
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        /*
         * 1. Set the allowed origins for the CORS requests.
         * 2. Allow any of the HTTP requests since it's a CRUD API. Also allow 'OPTIONS' to support preflight
         * 3. Allow all headers, we don't want to replace restrictions on those; you may also just do only Content-Type header and 'X-CSRF' if you want.
         * 4. Allow cookies to be included in cross-origin requests. This allows browsers to include cookies in a request, and for the server to set cookies for the client
         */
        corsConfig.setAllowedOrigins(List.of("http://localhost:5173/"));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "PUT", "PATCH", "POST", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setAllowCredentials(true);

        // Apply our Cors setting to all routes
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        return source;
    }
}

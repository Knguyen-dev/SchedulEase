package com.knguyendev.api.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * Class that redirects 'AuthenticationException' exceptions to our ControllerAdvice class
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {

    /**
     * resolver: Handles 'redirecting' the AuthenticationException to our ControllerAdvice class; specify the specific
     * NOTE: Have to put a qualifier since there can be multiple types of 'HandlerExceptionResolver' and we want a specific one.
     */
    private final HandlerExceptionResolver resolver;
    public AuthenticationEntryPointImpl(@Qualifier(value = "handlerExceptionResolver") HandlerExceptionResolver resolver) {
        this.resolver = resolver;
    }


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        this.resolver.resolveException(request, response, null, e);

    }
}

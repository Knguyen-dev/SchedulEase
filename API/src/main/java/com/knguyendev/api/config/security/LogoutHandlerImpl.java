package com.knguyendev.api.config.security;

import com.knguyendev.api.services.LogoutService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class LogoutHandlerImpl implements LogoutHandler {

    // Expecting a bean for the logout service implementation we created.
    private final LogoutService logoutService;
    public LogoutHandlerImpl(LogoutService logoutService) {
        this.logoutService = logoutService;
    }


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logoutService.logout(request, response);
    }
}



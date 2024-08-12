package com.knguyendev.api.config.security;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {


        // Check if authentication object is not null
        String message;
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            message = "Logout successful, goodbye '" + userDetails.getUsername() + "'!";
        } else {
            message = "Logout successful, but no one was logged out really!";
        }

        // Set the response content type and message
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);

        // Write the response
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}

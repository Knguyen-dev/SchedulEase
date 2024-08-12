package com.knguyendev.api.services;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Service class that provides a method for logging out the currently authenticated user. Note: We keep it this logic
 * in a separate class to avoid circular dependencies. When we place our logout logic in the AuthService we get a
 * circular dependency.
 */
public interface LogoutService {

    /**
     * Service method used to log out the currently authenticated user
     * @param request Really useful for getting the session ID and managing invalidating the session.
     * @param response Very useful for deleting the session cookie that we placed in the user's browser.
     */
    public void logout(HttpServletRequest request, HttpServletResponse response);
}

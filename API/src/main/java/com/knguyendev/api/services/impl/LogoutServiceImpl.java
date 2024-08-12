package com.knguyendev.api.services.impl;

import com.knguyendev.api.services.LogoutService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class LogoutServiceImpl implements LogoutService {
    private final FindByIndexNameSessionRepository<? extends Session> redisIndexedSessionRepository;
    @Value("${server.servlet.session.cookie.name}")
    private String sessionCookieName;

    // Expecting the RedisIndexSessionRepository bean
    public LogoutServiceImpl(RedisIndexedSessionRepository redisIndexedSessionRepository) {
        this.redisIndexedSessionRepository = redisIndexedSessionRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {

        // Attempts to get an existing HTTP session; without creating a new one.
        HttpSession userSession = request.getSession(false);

        // if a session exists for the user, delete it locally and from redis.
        if (userSession != null) {
            // Delete the session id from redis
            String sessionID = userSession.getId();
            if (sessionID != null && this.redisIndexedSessionRepository.findById(sessionID) != null) {
                this.redisIndexedSessionRepository.deleteById(sessionID);
            }

            // Invalidates the session locally; as a result it will no longer be valid server-side and subsequent requests
            // won't be able to use this session.
            userSession.invalidate();
        }

        /*
         * + Note about cookies:
         * 1. Cookies can only be deleted if the 'path' and 'domain' specified when setting the cookie matches the same ones
         * when deleting the cookie. So to delete a cookie you need to specify the same path and domain when you set it originally.
         * 2. Cookies with the 'SameSite' have restrictions. We may have trouble accessing them.
         * 3. HttpOnly: Cookies with the HttpOnly flag can only be accessed by server-side code.
         */
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (sessionCookieName.equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }

        /*
         * Clears the SecurityContextHolder, which is a thread-local storage used by Spring Security to hold the security
         * context of the currently authenticated user. By clearing the SecurityContextHolder, all information about the
         * authenticated user is removed for subsequent requests, not the current request. This just ensures
         * our app doesn't treat the user as authenticated on after our logout request.
         */
        SecurityContextHolder.clearContext();
    }
}
package com.knguyendev.api.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.stereotype.Component;

@Component
public class LogoutHandlerImpl implements LogoutHandler {
    private final FindByIndexNameSessionRepository<? extends Session> redisIndexedSessionRepository;
    public LogoutHandlerImpl(RedisIndexedSessionRepository redisIndexedSessionRepository) {
        this.redisIndexedSessionRepository = redisIndexedSessionRepository;
    }


    /**
     * Deletes a user's session from the redis session store.
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        /*
         * 1. Get the session ID of the current session. The 'false' argument ensures that we don't create a brand new session
         * if there are no sessions associated with the request object.
         * 2. If we found a sessionID on the request object, and we found a key-value pair in the session store, then
         * delete that key-value pair
         */
        String sessionID = request.getSession(false).getId();
        if (sessionID != null && this.redisIndexedSessionRepository.findById(sessionID) != null) {
            this.redisIndexedSessionRepository.deleteById(sessionID);
        }
    }
}



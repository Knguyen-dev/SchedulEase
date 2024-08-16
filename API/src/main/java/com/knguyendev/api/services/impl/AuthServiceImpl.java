package com.knguyendev.api.services.impl;

import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.User.UserDetailsImpl;
import com.knguyendev.api.domain.dto.User.UserLoginDTO;
import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.domain.entities.TaskListEntity;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.enumeration.UserRole;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.mappers.UserMapper;
import com.knguyendev.api.repositories.TaskListRepository;
import com.knguyendev.api.repositories.UserRepository;
import com.knguyendev.api.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Value(value = "${spring.custom.maxSession}")
    private int maxSession;

    private final UserRepository userRepository;
    private final TaskListRepository taskListRepository;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;
    private final RedisIndexedSessionRepository redisIndexedSessionRepository;
    private final SessionRegistry sessionRegistry;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    public AuthServiceImpl(
            UserRepository userRepository,
            TaskListRepository taskListRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authManager,
            RedisIndexedSessionRepository redisIndexedSessionRepository,
            SessionRegistry sessionRegistry,
            SecurityContextRepository securityContextRepository
    ) {
        this.userRepository = userRepository;
        this.taskListRepository = taskListRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authManager = authManager;
        this.redisIndexedSessionRepository = redisIndexedSessionRepository;
        this.sessionRegistry = sessionRegistry;
        this.securityContextRepository = securityContextRepository;
        this.securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    }



    @Override
    public UserDTO registerUser(UserRegistrationDTO userRegistrationDTO, UserRole role) throws ServiceException {
        // Ensure email and username are unique; query for a user where the username or email matches.
        Optional<UserEntity> result = userRepository.findByUsernameOrEmail(
                userRegistrationDTO.getUsername(),
                userRegistrationDTO.getEmail()
        );
        if (result.isPresent()) {
            UserEntity existingUser = result.get();
            if (existingUser.getUsername().equals(userRegistrationDTO.getUsername())) {
                throw new ServiceException("Username is already taken!", HttpStatus.BAD_REQUEST);
            } else {
                throw new ServiceException("Email is already in use!", HttpStatus.BAD_REQUEST);
            }
        }

        // Convert into an entity, apply the role, password hash, and creation time before saving it to the database
        UserEntity newUser = userMapper.toEntity(userRegistrationDTO);
        newUser.setRole(role);
        newUser.setPassword(passwordEncoder.encode(userRegistrationDTO.getPassword()));
        newUser.setCreatedAt(ZonedDateTime.now(ZoneId.of("UTC")));

        // Save the user and create the default task list for said user
        newUser = userRepository.save(newUser);
        createDefaultTaskList(newUser.getId());

        return userMapper.toDTO(newUser);
    }

    @Override
    public UserDTO loginUser(UserLoginDTO userLoginDTO, HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        /*
         * Check if the user is already authenticated, then just return the function early. This prevents
         * a client or user from logging in again if they're already logged in.
         */
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        if (existingAuth != null && existingAuth.isAuthenticated() && existingAuth.getPrincipal() instanceof UserDetailsImpl userDetails) {
            throw new AuthenticationException("You are already logged in as '" + userDetails.getUsername() + "'. Please log out before logging in again.") {};
        }

        // Create an 'Authentication' (an implementation of one) object that contains user info for logging in; it hasn't been verified yet.
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                userLoginDTO.getUsername(),
                userLoginDTO.getPassword()
        );

        // Attempt to verify our 'Authentication' object using an 'AuthenticationManager'; if credentials are incorrect,
        // then throw an 'AuthenticationException'
        Authentication auth = authManager.authenticate(token);

        // Credentials are valid, but ensure that authenticated sessions are managed
        validateMaxSession(auth, request) ;

        /*
         * + Setting up the SecurityContext for the logged-in user
         * 1. Create an empty security context, and then we'll place that Authentication object in it.
         * 2. Set our securityContext in the holder strategy, which is just a component that 'holds' the security
         * context for the currently authenticated user.
         * 3. Then save that context in the repository, which will allow that SecurityContext (the user's auth data) to be
         * stored in a http session.
         */
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        this.securityContextHolderStrategy.setContext(context);
        this.securityContextRepository.saveContext(context, request, response);
        Object principal = auth.getPrincipal();
        UserDTO userDTO = null;
        if (principal instanceof UserDetailsImpl userDetails) {
            UserEntity loggedInUser = userDetails.getUser();
            userDTO = userMapper.toDTO(loggedInUser);
        }

        return userDTO;
    }


    private void createDefaultTaskList(Long userId) {
        TaskListEntity defaultTaskList = TaskListEntity.builder()
                .userId(userId) // Set the user to the newly saved user
                .name("My Tasks") // Set a default name or any required fields
                .isDefault(true) // Mark this task list as default
                .build();

        taskListRepository.save(defaultTaskList);
    }


    /**
     * Method responsible for validating whether the number of sessions for a particular user hasn't been exceeded. If it has, then
     * the oldest valid session is invalidated to maintain order. This is used after we found our user via the UserDetailsService.
     *
     * @param authentication Authentication object that contains the existing user's information
     */
    private void validateMaxSession(Authentication authentication, HttpServletRequest request) {
        // If max session is negative means unlimited session
        if (maxSession <= 0) {
            return;
        }

        /*
         * +  Invalidate the current session if it exists
         * When the user logs in, we'll invalidate the current user's session. Without doing
         * this it can lead to inconsistent session states when the user
         *
         * NOTE: This prevents the SecurityContext from becoming blank after a user logs in
         * twice consecutively without logging out. Of course the user won't be able to log in
         * twice consecutively due to our conditional above.
         */
        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }

        // Get the principal (user details) from the authentication object
        var principal = (UserDetails) authentication.getPrincipal();
        List<SessionInformation> sessions = this.sessionRegistry.getAllSessions(principal, false);

        // If the number of sessions exceeds the max session limit, invalidate the oldest session
        if (sessions.size() >= maxSession) {
            sessions.stream()
                    .min(Comparator.comparing(SessionInformation::getLastRequest))
                    .ifPresent(sessionInfo -> this.redisIndexedSessionRepository.deleteById(sessionInfo.getSessionId()));
        }
    }
}

package com.knguyendev.api.services;


import com.knguyendev.api.TestUtil;
import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.enumeration.UserRole;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.mappers.UserMapper;
import com.knguyendev.api.repositories.UserRepository;
import com.knguyendev.api.services.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {

    // Service we are testing
    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authManager;
    @Mock
    private RedisIndexedSessionRepository redisIndexedSessionRepository;
    @Mock
    private SessionRegistry sessionRegistry;
    @Mock
    private SecurityContextRepository securityContextRepository;
    @Mock
    private SecurityContextHolderStrategy securityContextHolderStrategy;

    @Test
    public void testRegisterUserWhenUsernameTaken() {
        // Arrange
        UserEntity userA = TestUtil.createSavedUserA();
        UserRegistrationDTO registrationDTO = TestUtil.createUserRegistrationDTOA();
        UserRole role = UserRole.USER;

        // Simulate
        when(userRepository.findByUsernameOrEmail(
                registrationDTO.getUsername(),
                registrationDTO.getEmail()
        )).thenReturn(Optional.of(userA));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> authService.registerUser(registrationDTO, role));

        // Assert and verify
        assertEquals("Username is already taken!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(userRepository, times(1))
                .findByUsernameOrEmail(registrationDTO.getUsername(), registrationDTO.getEmail());
    }

    @Test
    public void testRegisterUserWhenEmailTaken() {
        // Arrange; ensure the username of userA and registrationDTO are different
        UserEntity userA = TestUtil.createSavedUserA();
        UserRegistrationDTO registrationDTO = TestUtil.createUserRegistrationDTOA();
        registrationDTO.setUsername("DifferentUsername");

        UserRole role = UserRole.USER;

        // Simulate
        when(userRepository.findByUsernameOrEmail(
                registrationDTO.getUsername(),
                registrationDTO.getEmail()
        )).thenReturn(Optional.of(userA));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> authService.registerUser(registrationDTO, role));

        // Assert and verify
        assertEquals("Email is already in use!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(userRepository, times(1))
                .findByUsernameOrEmail(registrationDTO.getUsername(), registrationDTO.getEmail());

    }

}

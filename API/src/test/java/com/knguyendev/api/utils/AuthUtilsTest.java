package com.knguyendev.api.utils;

import com.knguyendev.api.TestUtil;
import com.knguyendev.api.domain.dto.User.UserDetailsImpl;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AuthUtils class
 */
@ExtendWith(MockitoExtension.class)
public class AuthUtilsTest {

    @InjectMocks
    private AuthUtils authUtils;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetailsImpl userDetails;

    @Test
    public void testGetAuthUserIdWhenSuccess() {
        // Arrange
        UserEntity userA = TestUtil.createSavedUserA();

        // Simulate the idea of having an authenticated user
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(userA);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        Long userId = authUtils.getAuthUserId();

        // Assert
        assertEquals(userId, userA.getId());
    }

    @Test
    public void testGetAuthUserIdWhenNotAuthenticated() {
        // Arrange such that the authentication object is null
        SecurityContextHolder.getContext().setAuthentication(null);

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            authUtils.getAuthUserId();
        });

        // Assert
        assertEquals("User is not authenticated!", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
    }


    @Test
    public void testGetAuthUserIdWhenInvalidPrincipal() {
        // Arrange
        when(authentication.getPrincipal()).thenReturn(new Object()); // Return an invalid principal
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            authUtils.getAuthUserId();
        });

        // Assert
        assertEquals("User is not authenticated!", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
    }
}

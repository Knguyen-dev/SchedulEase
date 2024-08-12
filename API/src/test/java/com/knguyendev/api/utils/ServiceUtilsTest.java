package com.knguyendev.api.utils;

import com.knguyendev.api.TestUtil;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // allows us to mock in our tests
public class ServiceUtilsTest {

    @InjectMocks
    private ServiceUtils serviceUtils;

    @Mock
    private UserRepository userRepository;


    @Test
    public void testGetUserByIdWhenFound() {
        // Arrange
        UserEntity userA = TestUtil.createSavedUserA();

        // Simulate finding a user
        when(userRepository.findById(userA.getId())).thenReturn(Optional.of(userA));

        // Act
        UserEntity result = serviceUtils.getUserById(userA.getId());

        // Assert
        assertEquals(result, userA);

        // Verify
        verify(userRepository, times(1)).findById(userA.getId());
    }

    @Test
    public void testGetUserByIdWhenNotFound() {
        // Arrange
        UserEntity userA = TestUtil.createSavedUserA();

        // Simulate finding a user
        when(userRepository.findById(userA.getId())).thenReturn(Optional.empty());

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> serviceUtils.getUserById(userA.getId()));

        // Assert
        assertEquals("User with ID '" + userA.getId() + "' not found!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userRepository, times(1)).findById(userA.getId());
    }
}

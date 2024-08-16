package com.knguyendev.api.services;

import com.knguyendev.api.TestUtil;
import com.knguyendev.api.domain.dto.UserRelationship.UserRelationshipDTO;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.domain.entities.UserRelationshipEntity;
import com.knguyendev.api.enumeration.UserRelationshipStatus;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.mappers.UserRelationshipMapper;
import com.knguyendev.api.repositories.UserRelationshipRepository;
import com.knguyendev.api.repositories.UserRepository;
import com.knguyendev.api.services.impl.UserRelationshipServiceImpl;
import com.knguyendev.api.utils.AuthUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserRelationshipServiceImplTest {

    // Service we are testing
    @InjectMocks
    private UserRelationshipServiceImpl userRelationshipService;

    // Dependencies we are mocking
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRelationshipRepository userRelationshipRepository;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private UserRelationshipMapper userRelationshipMapper;

    @Test
    public void testRequestFriendshipWhenPendingFirstSecond_AuthUserIsFirstUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();

        // authUser is secondUser, whilst targetUser is firstUser
        authUser.setId(1L);
        targetUser.setId(2L);

        // This relationship indicates that the auth. user sent the friend request to the target user
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.PENDING_FIRST_SECOND)
                .build();

        // Simulating the database query to say we found a relationship
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.requestFriendship(targetUser.getId()));

        // Assert
        assertEquals("A pending friend request has already been sent to '" + targetUser.getUsername() + "'!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // Verify
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testRequestFriendshipWhenPendingFirstSecond_AuthUserIsSecondUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();

        // authUser is secondUser, whilst targetUser is firstUser
        authUser.setId(2L);
        targetUser.setId(1L);

        // This relationship indicates that the auth. user sent the friend request to the target user
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(targetUser)
                .secondUser(authUser)
                .status(UserRelationshipStatus.PENDING_FIRST_SECOND)
                .build();

        // Simulating the database query to say we found a relationship
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.requestFriendship(targetUser.getId()));

        // Assert
        assertEquals("You already have a pending friend request from '" + targetUser.getUsername() + "'! Please accept or decline that request!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // Verify
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        );
    }

    @Test
    public void testRequestFriendshipWhenPendingSecondFirst_AuthUserIsFirstUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        // This relationship indicates that the auth. user sent the friend request to the target user
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.PENDING_SECOND_FIRST)
                .build();

        // Simulating the database query to say we found a relationship
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.requestFriendship(targetUser.getId()));

        // Assert
        assertEquals("You already have a pending friend request from '" + targetUser.getUsername() + "'! Please accept or decline that request!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // Verify
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testRequestFriendshipWhenPendingSecondFirst_AuthUserIsSecondUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();

        // authUser is secondUser, whilst targetUser is firstUser
        authUser.setId(2L);
        targetUser.setId(1L);

        // This relationship indicates that the auth. user sent the friend request to the target user
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(targetUser)
                .secondUser(authUser)
                .status(UserRelationshipStatus.PENDING_SECOND_FIRST)
                .build();

        // Simulating the database query to say we found a relationship
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.requestFriendship(targetUser.getId()));

        // Assert
        assertEquals("A pending friend request has already been sent to '" + targetUser.getUsername() + "'!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // Verify
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        );

    }

    @Test
    public void testRequestFriendWhenSuccess_AuthUserIsFirstUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.PENDING_FIRST_SECOND)
                .build();

        UserRelationshipDTO expectedDTO = TestUtil.createRelationshipDTO(relationship);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());

        // Simulating idea that we didn't find a user relationship
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.empty());
        when(userRepository.findByIds(List.of(authUser.getId(), targetUser.getId()))).thenReturn(
                List.of(authUser, targetUser)
        );
        // Simulate saving UserRelationship into the database; verifies an unsaved user entity was passed in
        when(userRelationshipRepository.save(any(UserRelationshipEntity.class))).thenReturn(
                relationship
        );
        when(userRelationshipMapper.toDTO(relationship)).thenReturn(expectedDTO);

        // Act
        UserRelationshipDTO resultDTO = userRelationshipService.requestFriendship(targetUser.getId());

        // Assert
        assertEquals(expectedDTO, resultDTO);
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
        verify(userRepository, times(1)).findByIds(List.of(authUser.getId(), targetUser.getId()));
        verify(userRelationshipRepository, times(1)).save(any(UserRelationshipEntity.class));
        verify(userRelationshipMapper, times(1)).toDTO(relationship);
    }

    @Test
    public void testRequestFriendshipWhenAlreadyFriends() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();

        // Just making it clear that authUser is the firstUser
        authUser.setId(1L);
        targetUser.setId(2L);
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.FRIENDS)
                .build();

        Long firstUserId = Math.min(authUser.getId(), targetUser.getId());
        Long secondUserId = Math.max(authUser.getId(), targetUser.getId());

        // Simulating the database query to say we found a relationship
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                firstUserId,
                secondUserId
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.requestFriendship(targetUser.getId()));

        // Assert
        assertEquals("Can't send a friend request since you are already friends with this user!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // Verify
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                firstUserId,
                secondUserId
        );
    }

    @Test
    public void testRequestFriendshipWhenBlockFirstSecond_AuthUserIsFirstUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.BLOCK_FIRST_SECOND)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.requestFriendship(targetUser.getId()));

        // Assert
        assertEquals("You have blocked '" + targetUser.getUsername() + "'. Unblock them to send a friend request!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // Assert and Verify
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testRequestFriendshipWhenBlockFirstSecond_AuthUserIsSecondUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(2L);
        targetUser.setId(1L);
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(targetUser)
                .secondUser(authUser)
                .status(UserRelationshipStatus.BLOCK_FIRST_SECOND)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.requestFriendship(targetUser.getId()));

        // Assert
        assertEquals("'" + targetUser.getUsername() + "' has blocked you. They must unblock you before you can send a friend request.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // Assert and Verify
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        );
    }

    @Test
    public void testRequestFriendshipWhenBlockSecondFirst_AuthUserIsFirstUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.BLOCK_SECOND_FIRST)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.requestFriendship(targetUser.getId()));

        // Assert
        assertEquals("'" + targetUser.getUsername() + "' has blocked you. They must unblock you before you can send a friend request.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // Assert and Verify
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );

    }

    @Test
    public void testRequestFriendshipWhenBlockSecondFirst_AuthUserIsSecondUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(2L);
        targetUser.setId(1L);
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(targetUser)
                .secondUser(authUser)
                .status(UserRelationshipStatus.BLOCK_SECOND_FIRST)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.requestFriendship(targetUser.getId()));

        // Assert
        assertEquals("You have blocked '" + targetUser.getUsername() + "'. Unblock them to send a friend request!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // Assert and Verify
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        );
    }

    // Result is the same whether the auth. user is the first or second user
    @Test
    public void testRequestFriendshipWhenBlockBoth_AuthUserIsFirstUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.BLOCK_BOTH)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.requestFriendship(targetUser.getId()));

        // Assert
        assertEquals("You have blocked '" + targetUser.getUsername() + "'. Unblock them to send a friend request!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // Assert and Verify
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testAcceptFriendRequestWhenNoRelationship() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.empty());

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.acceptFriendRequest(targetUser.getId()));

        // Assert
        assertEquals("A pending friend request wasn't found!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

        // Assert and Verify
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testAcceptFriendRequestWhenNonPendingRelationship() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                // Simulating that the users have 'friendship' user-relationship
                .status(UserRelationshipStatus.FRIENDS)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.acceptFriendRequest(targetUser.getId()));

        // Assert
        assertEquals("A pending friend request wasn't found!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());

        // Assert and Verify
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testAcceptFriendRequestWhenPendingFirstSecond_AuthUserIsFirstUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                // Simulating that the users have 'friendship' user-relationship
                .status(UserRelationshipStatus.PENDING_FIRST_SECOND)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.acceptFriendRequest(targetUser.getId()));

        // Assert
        assertEquals("You cannot accept this friend request since you're the one who sent it!", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());

        // Assert and Verify
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testAcceptFriendRequestWhenPendingSecondFirst_AuthUserIsSecondUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(2L);
        targetUser.setId(1L);
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(targetUser)
                .secondUser(authUser)
                // Simulating that the users have 'friendship' user-relationship
                .status(UserRelationshipStatus.PENDING_SECOND_FIRST)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.acceptFriendRequest(targetUser.getId()));

        // Assert
        assertEquals("You cannot accept this friend request since you're the one who sent it!", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());

        // Assert and Verify
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        );
    }

    @Test
    public void testDeleteFriendRequestWhenNoRelationship() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.empty());

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.deleteFriendRequest(targetUser.getId()));

        // Assert & Verify
        assertEquals("A pending friend request wasn't found!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testDeleteFriendRequestWhenNoPendingRequest() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
            .id(1L)
            .firstUser(authUser)
            .secondUser(targetUser)
            .status(UserRelationshipStatus.FRIENDS)
            .build();


        // Simulate the idea that a relationship exists, but it's not a pending friendship
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.deleteFriendRequest(targetUser.getId()));

        // Assert & Verify
        assertEquals("A pending friend request wasn't found!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testDeleteFriendshipWhenNoRelationship() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        // Simulate getting no relationship from query
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.empty());

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.deleteFriendship(targetUser.getId()));

        // Assert & Verify
        assertEquals("Friendship being deleted wasn't found!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testDeleteFriendshipWhenNoFriendship() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.PENDING_FIRST_SECOND)
                .build();

        // Simulate getting a relationship that doesn't have the friendship status
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.deleteFriendship(targetUser.getId()));

        // Assert & Verify
        assertEquals("Friendship being deleted wasn't found!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testBlockUserWhenNoRelationship() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        UserRelationshipEntity unsavedRelationship = UserRelationshipEntity.builder()
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.BLOCK_FIRST_SECOND)
                .build();
        // Auth. user is blocking the target user in this relationship
        UserRelationshipEntity savedRelationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.BLOCK_FIRST_SECOND)
                .build();

        UserRelationshipDTO expectedDTO = TestUtil.createRelationshipDTO(savedRelationship);

        // Simulate: Simulating that there wasn't an existing user relationship
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.empty());
        when(userRepository.findByIds(List.of(authUser.getId(), targetUser.getId()))).thenReturn(List.of(authUser, targetUser));
        when(userRelationshipRepository.save(unsavedRelationship)).thenReturn(savedRelationship);
        when(userRelationshipMapper.toDTO(savedRelationship)).thenReturn(expectedDTO);

        // Act
        UserRelationshipDTO resultDTO = userRelationshipService.blockUser(targetUser.getId());

        // Assert and Verify
        assertEquals(expectedDTO, resultDTO);
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
        verify(userRepository, times(1)).findByIds(List.of(authUser.getId(), targetUser.getId()));
        verify(userRelationshipRepository, times(1)).save(unsavedRelationship);
        verify(userRelationshipMapper, times(1)).toDTO(savedRelationship);
    }

    @Test
    public void testBlockUserWhenNonBlockedRelationship() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        // Auth. user is blocking the target user in this relationship
        UserRelationshipEntity unsavedRelationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.FRIENDS)
                .build();
        UserRelationshipEntity savedRelationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.BLOCK_FIRST_SECOND)
                .build();

        UserRelationshipDTO expectedDTO = TestUtil.createRelationshipDTO(savedRelationship);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(unsavedRelationship));
        when(userRelationshipRepository.save(unsavedRelationship)).thenReturn(savedRelationship);
        when(userRelationshipMapper.toDTO(savedRelationship)).thenReturn(expectedDTO);

        // Act
        UserRelationshipDTO resultDTO = userRelationshipService.blockUser(targetUser.getId());

        // Assert and Verify
        assertEquals(expectedDTO, resultDTO);
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
        verify(userRelationshipRepository, times(1)).save(unsavedRelationship);
        verify(userRelationshipMapper, times(1)).toDTO(savedRelationship);
    }

    @Test
    public void testBlockUserWhenBlockFirstSecond_AuthUserIsFirstUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        // Auth. user is blocking the target user in this relationship
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.BLOCK_FIRST_SECOND)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.blockUser(targetUser.getId()));

        // Assert and Verify
        assertEquals("You already have this user blocked!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testBlockUserWhenBlockFirstSecond_AuthUserIsSecondUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(2L);
        targetUser.setId(1L);

        // Auth. user is blocking the target user in this relationship
        UserRelationshipEntity unsavedRelationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(targetUser)
                .secondUser(authUser)
                .status(UserRelationshipStatus.BLOCK_FIRST_SECOND)
                .build();

        UserRelationshipEntity savedRelationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(targetUser)
                .secondUser(authUser)
                .status(UserRelationshipStatus.BLOCK_BOTH)
                .build();

        UserRelationshipDTO expectedDTO = TestUtil.createRelationshipDTO(savedRelationship);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        )).thenReturn(Optional.of(unsavedRelationship));
        when(userRelationshipRepository.save(unsavedRelationship)).thenReturn(savedRelationship);
        when(userRelationshipMapper.toDTO(savedRelationship)).thenReturn(expectedDTO);

        UserRelationshipDTO resultDTO = userRelationshipService.blockUser(targetUser.getId());

        // Assert and Verify
        assertEquals(expectedDTO, resultDTO);
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        );
        verify(userRelationshipRepository, times(1)).save(unsavedRelationship);
        verify(userRelationshipMapper, times(1)).toDTO(savedRelationship);
    }

    @Test
    public void testBlockUserWhenBlockSecondFirst_AuthUserIsFirstUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        // Auth. user is blocking the target user in this relationship
        UserRelationshipEntity unsavedRelationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.BLOCK_SECOND_FIRST)
                .build();

        UserRelationshipEntity savedRelationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.BLOCK_BOTH)
                .build();

        UserRelationshipDTO expectedDTO = TestUtil.createRelationshipDTO(savedRelationship);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(unsavedRelationship));
        when(userRelationshipRepository.save(unsavedRelationship)).thenReturn(savedRelationship);
        when(userRelationshipMapper.toDTO(savedRelationship)).thenReturn(expectedDTO);

        UserRelationshipDTO resultDTO = userRelationshipService.blockUser(targetUser.getId());

        // Assert and verify
        assertEquals(expectedDTO, resultDTO);
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
        verify(userRelationshipRepository, times(1)).save(unsavedRelationship);
        verify(userRelationshipMapper, times(1)).toDTO(savedRelationship);
    }

    @Test
    public void testBlockUserWhenBlockSecondFirst_AuthUserIsSecondUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(2L);
        targetUser.setId(1L);

        // Auth. user is blocking the target user in this relationship
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(targetUser)
                .secondUser(authUser)
                .status(UserRelationshipStatus.BLOCK_SECOND_FIRST)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.blockUser(targetUser.getId()));

        // Assert and Verify
        assertEquals("You already have this user blocked!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        );
    }

    @Test
    public void testBlockUserWhenBlockBoth() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        // Auth. user is blocking the target user in this relationship
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.BLOCK_BOTH)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.blockUser(targetUser.getId()));

        // Assert and Verify
        assertEquals("You already have this user blocked!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    // Then the test cases for unblocking
    @Test
    public void testUnblockUserWhenNoRelationship() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.empty());

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.unblockUser(targetUser.getId()));

        // Assert and Verify
        assertEquals("You haven't blocked this user, so you can't unblock them!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testUnblockUserWhenNotBlockedStatus() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        // Simulate relationship where auth user is receiving a friend request
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.PENDING_SECOND_FIRST)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.unblockUser(targetUser.getId()));

        // Assert and Verify
        assertEquals("You haven't blocked this user, so you can't unblock them!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testUnblockUserWhenBlockBoth() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        // Simulate relationship where auth user is receiving a friend request
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.BLOCK_BOTH)
                .build();

        UserRelationshipEntity newRelationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                // Auth user is doing the unblocking, so the relationship should now be the other user blocking the auth. user
                .status(UserRelationshipStatus.BLOCK_SECOND_FIRST)
                .build();

        UserRelationshipDTO expectedDTO = TestUtil.createRelationshipDTO(newRelationship);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));
        when(userRelationshipRepository.save(relationship)).thenReturn(newRelationship);
        when(userRelationshipMapper.toDTO(newRelationship)).thenReturn(expectedDTO);

        // Act
        UserRelationshipDTO resultDTO = userRelationshipService.unblockUser(targetUser.getId());

        // Assert and verify
        assertEquals(expectedDTO, resultDTO);
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
        verify(userRelationshipRepository, times(1)).save(relationship);
        verify(userRelationshipMapper, times(1)).toDTO(newRelationship);
    }

    @Test
    public void testUnblockUserWhenBlockFirstSecond_AuthUserIsFirstUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        // Simulate relationship where auth user is receiving a friend request
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.BLOCK_FIRST_SECOND)
                .build();

        UserRelationshipDTO expectedDTO = TestUtil.createRelationshipDTO(relationship);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));
        when(userRelationshipMapper.toDTO(relationship)).thenReturn(expectedDTO);

        // Act
        UserRelationshipDTO resultDTO = userRelationshipService.unblockUser(targetUser.getId());

        // Assert and Verify
        assertEquals(expectedDTO,resultDTO);
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
        verify(userRelationshipRepository, times(1)).delete(
                relationship
        );
        verify(userRelationshipMapper, times(1)).toDTO(
                relationship
        );
    }

    @Test
    public void testUnblockUserWhenBlockFirstSecond_AuthUserIsSecondUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(2L);
        targetUser.setId(1L);

        // Simulate relationship where auth user is receiving a friend request
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(targetUser)
                .secondUser(authUser)
                .status(UserRelationshipStatus.BLOCK_FIRST_SECOND)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.unblockUser(targetUser.getId()));

        // Assert and Verify
        assertEquals("You haven't blocked this user, so you can't unblock them!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        );
    }

    @Test
    public void testUnblockUserWhenBlockSecondFirst_AuthUserIsFirstUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(1L);
        targetUser.setId(2L);

        // Simulate relationship where auth user is receiving a friend request
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(authUser)
                .secondUser(targetUser)
                .status(UserRelationshipStatus.BLOCK_SECOND_FIRST)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        )).thenReturn(Optional.of(relationship));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userRelationshipService.unblockUser(targetUser.getId()));

        // Assert and Verify
        assertEquals("You haven't blocked this user, so you can't unblock them!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                authUser.getId(),
                targetUser.getId()
        );
    }

    @Test
    public void testUnblockUserWhenBlockSecondFirst_AuthUserIsSecondUser() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        authUser.setId(2L);
        targetUser.setId(1L);

        // Simulate relationship where auth user is receiving a friend request
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(targetUser)
                .secondUser(authUser)
                .status(UserRelationshipStatus.BLOCK_SECOND_FIRST)
                .build();

        UserRelationshipDTO expectedDTO = TestUtil.createRelationshipDTO(relationship);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(userRelationshipRepository.findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        )).thenReturn(Optional.of(relationship));
        when(userRelationshipMapper.toDTO(relationship)).thenReturn(expectedDTO);

        // Act
        UserRelationshipDTO resultDTO = userRelationshipService.unblockUser(targetUser.getId());

        // Assert and Verify
        assertEquals(expectedDTO, resultDTO);
        verify(userRelationshipRepository, times(1)).findByFirstUserIdAndSecondUserId(
                targetUser.getId(),
                authUser.getId()
        );
        verify(userRelationshipRepository, times(1)).delete(relationship);
        verify(userRelationshipMapper, times(1)).toDTO(relationship);
    }
}

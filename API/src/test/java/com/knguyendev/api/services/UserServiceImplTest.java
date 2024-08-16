package com.knguyendev.api.services;

import com.knguyendev.api.TestUtil;
import com.knguyendev.api.domain.dto.User.ChangePasswordDTO;
import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.User.UserDeleteDTO;
import com.knguyendev.api.domain.dto.User.UserProfileUpdateDTO;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.enumeration.UserRole;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.mappers.UserMapper;
import com.knguyendev.api.repositories.TaskListRepository;
import com.knguyendev.api.repositories.UserRelationshipRepository;
import com.knguyendev.api.repositories.UserRepository;
import com.knguyendev.api.services.impl.UserServiceImpl;
import com.knguyendev.api.utils.AuthUtils;
import com.knguyendev.api.utils.ServiceUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // allows us to mock in our tests
public class UserServiceImplTest {
    // Define the service we're testing
    @InjectMocks
    private UserServiceImpl userService;

    // Mock all outside dependencies that the service uses
    @Mock
    private UserRepository userRepository;
    @Mock
    TaskListRepository taskListRepository;
    @Mock
    UserRelationshipRepository userRelationshipRepository;

    @Mock
    private UserMapper userMapper;
    @Mock
    private LogoutService logoutService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private ServiceUtils serviceUtils;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Test
    public void testFindById() {
        // Arrange
        UserEntity userA = TestUtil.createSavedUserA();
        UserDTO expectedDTO = TestUtil.createUserDTOA();

        // Simulate querying and mapping
        when(serviceUtils.getUserById(userA.getId())).thenReturn(userA);
        when(userMapper.toDTO(userA)).thenReturn(expectedDTO);

        // Act
        UserDTO resultDTO = userService.findById(userA.getId());

        // Assert
        assertEquals(expectedDTO, resultDTO);
        verify(serviceUtils, times(1)).getUserById(userA.getId());
        verify(userMapper, times(1)).toDTO(userA);
    }

    @Test
    public void testFindAll() {
        // Arrange
        UserEntity userA = TestUtil.createSavedUserA();
        UserEntity userB = TestUtil.createSavedUserB();
        UserDTO userDTOA = TestUtil.createUserDTOA();
        UserDTO userDTOB = TestUtil.createUserDTOB();
        List<UserEntity> users = List.of(userA, userB);

        // Simulate finding and mapping
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDTO(userA)).thenReturn(userDTOA);
        when(userMapper.toDTO(userB)).thenReturn(userDTOB);

        // Act
        List<UserDTO> result = userService.findAll();

        // Assert and verify
        assertEquals(users.size(), result.size());
        assertEquals(userDTOA, result.get(0));
        assertEquals(userDTOB, result.get(1));

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toDTO(userA);
        verify(userMapper, times(1)).toDTO(userB);
    }

    @Test
    public void getAuthenticatedUser() {
        // Arrange
        UserEntity userA = TestUtil.createSavedUserA();
        UserDTO userDTOA = TestUtil.createUserDTOA();

        // Simulate the authUtils, querying, and
        when(authUtils.getAuthUserId()).thenReturn(userA.getId());
        when(serviceUtils.getUserById(userA.getId())).thenReturn(userA);
        when(userMapper.toDTO(userA)).thenReturn(userDTOA);

        // Act
        UserDTO resultDTO = userService.getAuthenticatedUser();

        // Assert
        assertEquals(userDTOA, resultDTO);
        verify(authUtils, times(1)).getAuthUserId();
        verify(serviceUtils, times(1)).getUserById(userA.getId());
        verify(userMapper, times(1)).toDTO(userA);
    }

    @Test
    public void testUpdateAccountProfileWhenSuccess() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();

        UserProfileUpdateDTO updateDTO = UserProfileUpdateDTO.builder()
                .username("NewUsername")
                .email("NewEmail")
                .firstName("NewFirstName")
                .lastName("NewLastName")
                .biography("NewBiography")
                .build();

        // Create the 'updatedUser', which is just the authenticated user with the information from the updateDTO
        UserEntity updatedUser = TestUtil.createSavedUserA();
        updatedUser.setUsername(updateDTO.getUsername());
        updatedUser.setEmail(updateDTO.getEmail());
        updatedUser.setFirstName(updateDTO.getFirstName());
        updatedUser.setLastName(updateDTO.getLastName());
        updatedUser.setBiography(updateDTO.getBiography());

        // Create the DTO that should be returned when mapping the 'updatedUser' into a DTO
        UserDTO expectedDTO = TestUtil.createUserDTO(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getEmail(),
                updatedUser.getFirstName(),
                updatedUser.getLastName(),
                updatedUser.getBiography(),
                updatedUser.getIsVerified(),
                updatedUser.getCreatedAt(),
                updatedUser.getRole()
        );

        // Simulate no conflict when finding and updating the user
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(serviceUtils.getUserById(authUser.getId())).thenReturn(authUser);
        when(userRepository.findByUsername(updateDTO.getUsername())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(authUser)).thenReturn(updatedUser);
        when(userMapper.toDTO(updatedUser)).thenReturn(expectedDTO);

        // Act
        UserDTO resultDTO = userService.updateAccountProfile(updateDTO);

        // Assert and verify
        assertEquals(resultDTO, expectedDTO);

        // We're expecting these to be called the username and emails should be different
        verify(userRepository, times(1)).findByUsername(updateDTO.getUsername());
        verify(userRepository, times(1)).findByEmail(updateDTO.getEmail());

        verify(userRepository, times(1)).save(updatedUser);
        verify(userMapper, times(1)).toDTO(updatedUser);
    }

    @Test
    public void testUpdateAccountProfileWhenNoChange() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserDTO expectedDTO = TestUtil.createUserDTOA();

        // Give updateDTO the same info as the authenticated user
        UserProfileUpdateDTO updateDTO = UserProfileUpdateDTO.builder()
                .username(authUser.getUsername())
                .email(authUser.getEmail())
                .firstName(authUser.getFirstName())
                .lastName(authUser.getLastName())
                .biography(authUser.getBiography())
                .build();

        // Simulate no conflict when finding and updating the user
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(serviceUtils.getUserById(authUser.getId())).thenReturn(authUser);
        when(userMapper.toDTO(authUser)).thenReturn(expectedDTO);

        // Act
        UserDTO resultDTO = userService.updateAccountProfile(updateDTO);

        // Assert and Verify
        assertEquals(resultDTO, expectedDTO);
        verify(userRepository, never()).findByUsername(updateDTO.getUsername());
        verify(userRepository, never()).findByEmail(updateDTO.getEmail());
        verify(userRepository, never()).save(authUser);
        verify(userMapper, times(1)).toDTO(authUser);
    }

    @Test
    public void testUpdateAccountProfileWhenConflictingUsername() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity existingUser = TestUtil.createSavedUserB();

        // Give updateDTO the new username
        UserProfileUpdateDTO updateDTO = UserProfileUpdateDTO.builder()
                .username(existingUser.getUsername())
                .email(authUser.getEmail())
                .firstName(authUser.getFirstName())
                .lastName(authUser.getLastName())
                .biography(authUser.getBiography())
                .build();

        // Simulate a situation where we find a taken username
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(serviceUtils.getUserById(authUser.getId())).thenReturn(authUser);
        when(userRepository.findByUsername(updateDTO.getUsername())).thenReturn(Optional.of(existingUser));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.updateAccountProfile(updateDTO));

        // Assert and verify
        assertEquals("Username '" + updateDTO.getUsername() + "' is already taken!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(userRepository, times(1)).findByUsername(updateDTO.getUsername());
        verify(userRepository, never()).findByEmail(updateDTO.getEmail());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userMapper, never()).toDTO(any(UserEntity.class));
    }

    @Test
    public void testUpdateAccountProfileWhenConflictingEmail() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity existingUser = TestUtil.createSavedUserB();

        // Give updateDTO the new email
        UserProfileUpdateDTO updateDTO = UserProfileUpdateDTO.builder()
                .username(authUser.getUsername())
                .email(existingUser.getEmail())
                .firstName(authUser.getFirstName())
                .lastName(authUser.getLastName())
                .biography(authUser.getBiography())
                .build();

        // Simulate a situation where we find a taken email
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(serviceUtils.getUserById(authUser.getId())).thenReturn(authUser);
        when(userRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.of(existingUser));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.updateAccountProfile(updateDTO));

        // Assert and verify
        assertEquals("Email '" + updateDTO.getEmail() + "' is already in use!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        verify(userRepository, never()).findByUsername(any(String.class));
        verify(userRepository, times(1)).findByEmail(updateDTO.getEmail());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(userMapper, never()).toDTO(any(UserEntity.class));
    }

    @Test
    public void testDeleteAccountWhenAdminRole() {
        UserEntity authUser = TestUtil.createSavedUserA();
        UserDeleteDTO deleteDTO = UserDeleteDTO.builder()
                .password(authUser.getPassword())
                .confirmPassword(authUser.getPassword())
                .build();

        // Set the role to administrator
        authUser.setRole(UserRole.ADMIN);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(serviceUtils.getUserById(authUser.getId())).thenReturn(authUser);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.deleteAccount(request, response, deleteDTO));

        // Assert
        assertEquals("Admins aren't allowed to delete their own accounts! Please get a 'Super Admin' to delete it for you.", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());

        verify(userRepository, never()).deleteById(anyLong());
        verify(logoutService, never()).logout(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(userMapper, never()).toDTO(any(UserEntity.class));
    }

    @Test
    public void testDeleteAccountWhenSuperAdminRole() {
        UserEntity authUser = TestUtil.createSavedUserA();
        UserDeleteDTO deleteDTO = UserDeleteDTO.builder()
                .password(authUser.getPassword())
                .confirmPassword(authUser.getPassword())
                .build();

        // Set the role to super admin
        authUser.setRole(UserRole.SUPER_ADMIN);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(serviceUtils.getUserById(authUser.getId())).thenReturn(authUser);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.deleteAccount(request, response, deleteDTO));

        // Assert
        assertEquals("Admins aren't allowed to delete their own accounts! Please get a 'Super Admin' to delete it for you.", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());

        // Verify no calls were made to deleteById, logout, or the mapper
        verify(userRepository, never()).deleteById(anyLong());
        verify(logoutService, never()).logout(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(userMapper, never()).toDTO(any(UserEntity.class));
    }

    @Test
    public void testDeleteAccountWhenBadPassword() {
        UserEntity authUser = TestUtil.createSavedUserA();
        UserDeleteDTO deleteDTO = UserDeleteDTO.builder()
                .password(authUser.getPassword())
                .confirmPassword(authUser.getPassword())
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(serviceUtils.getUserById(authUser.getId())).thenReturn(authUser);
        when(passwordEncoder.matches(deleteDTO.getPassword(), deleteDTO.getConfirmPassword())).thenReturn(false);

        // Act & Assert
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.deleteAccount(request, response, deleteDTO));

        // Assert
        assertEquals("Password you entered is incorrect, and doesn't match your current password!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // Verify no calls were made to deleteById, logout, or the mapper
        verify(userRepository, never()).deleteById(anyLong());
        verify(logoutService, never()).logout(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(userMapper, never()).toDTO(any(UserEntity.class));

    }

    @Test
    public void testDeleteAccountWhenSuccess() {
        UserEntity authUser = TestUtil.createSavedUserA();
        UserDTO expectedDTO = TestUtil.createUserDTOA();
        UserDeleteDTO deleteDTO = UserDeleteDTO.builder()
                .password(authUser.getPassword())
                .confirmPassword(authUser.getPassword())
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(serviceUtils.getUserById(authUser.getId())).thenReturn(authUser);
        when(passwordEncoder.matches(deleteDTO.getPassword(), deleteDTO.getConfirmPassword())).thenReturn(true);


        when(userMapper.toDTO(authUser)).thenReturn(expectedDTO);

        // Act
        UserDTO resultDTO = userService.deleteAccount(request, response, deleteDTO);

        // Assert and Verify
        assertEquals(expectedDTO, resultDTO);
        verify(userRepository, times(1)).deleteById(authUser.getId());
        verify(taskListRepository, times(1)).deleteByUserId(authUser.getId());
        verify(userRelationshipRepository, times(1)).deleteByUserId(authUser.getId());

        verify(logoutService, times(1)).logout(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(passwordEncoder, times(1)).matches(deleteDTO.getPassword(), deleteDTO.getConfirmPassword());



        verify(userMapper, times(1)).toDTO(authUser);

    }

    @Test
    public void testDeleteByIdWhenDeletingOwnAccount() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();

        // Simulate the authenticated user id
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.deleteById(authUser.getId()); // ID being deleted is the authenticated user
        });

        // Assert and verify
        assertEquals("You can't delete your own account!", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
        verify(serviceUtils, never()).getUserById(anyLong());
        verify(userRepository, never()).deleteById(anyLong());
        verify(userMapper, never()).toDTO(any(UserEntity.class));
    }

    @Test
    public void testDeleteByIdWhenDeletingSuperAdmin() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity superAdmin = TestUtil.createSavedUserB();
        superAdmin.setRole(UserRole.SUPER_ADMIN);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());

        // Simulate the idea of returning the super admin
        when(serviceUtils.getUserById(superAdmin.getId())).thenReturn(superAdmin);

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.deleteById(superAdmin.getId()); // ID being deleted is the super admin ID
        });

        // Assert and verify
        assertEquals("User is a 'Super Admin'. They cannot be deleted!", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
        verify(serviceUtils, times(1)).getUserById(superAdmin.getId());
        verify(userRepository, never()).deleteById(anyLong());
        verify(userMapper, never()).toDTO(any(UserEntity.class));
    }

    @Test
    public void testDeleteByIdWhenDeletingAdminWithoutPerms() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        authUser.setRole(UserRole.USER);
        UserEntity admin = TestUtil.createSavedUserB();
        admin.setRole(UserRole.ADMIN);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(serviceUtils.getUserById(admin.getId())).thenReturn(admin);
        when(serviceUtils.getUserById(authUser.getId())).thenReturn(authUser);

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> {
            userService.deleteById(admin.getId()); // ID being deleted is the super admin ID
        });

        // Assert and verify
        assertEquals("You must be a 'Super Admin' to be able to delete another admin!", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
        verify(serviceUtils, times(1)).getUserById(admin.getId());
        verify(serviceUtils, times(1)).getUserById(authUser.getId());
        verify(userRepository, never()).deleteById(anyLong());
        verify(userMapper, never()).toDTO(any(UserEntity.class));
    }

    @Test
    public void testDeleteByIdWhenSuccess() {
        // Arrange, we'll simulate the idea that we're deleting another user
        UserEntity authUser = TestUtil.createSavedUserA();
        UserEntity targetUser = TestUtil.createSavedUserB();
        targetUser.setRole(UserRole.USER);

        // Create the expected DTO from deleting 'User B'
        UserDTO expectedDTO = TestUtil.createUserDTOB();
        expectedDTO.setRole(UserRole.USER);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(serviceUtils.getUserById(targetUser.getId())).thenReturn(targetUser);
        when(userMapper.toDTO(targetUser)).thenReturn(expectedDTO);

        // Act
        UserDTO resultDTO = userService.deleteById(targetUser.getId());

        assertEquals(expectedDTO, resultDTO);
        verify(serviceUtils, times(1)).getUserById(targetUser.getId());
        verify(taskListRepository, times(1)).deleteByUserId(targetUser.getId());
        verify(userRelationshipRepository, times(1)).deleteByUserId(targetUser.getId());

        verify(userRepository, times(1)).deleteById(targetUser.getId());
        verify(userMapper, times(1)).toDTO(targetUser);
    }

    @Test
    public void testChangePasswordWhenNoChange() {
        // Arrange data such that the password and newPassword are the same
        String password = "BadPassword123";
        ChangePasswordDTO passwordDTO = ChangePasswordDTO.builder()
                .password(password)
                .newPassword(password)
                .confirmNewPassword(password)
                .build();

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.changePassword(request, response, passwordDTO));

        // Assert and verify: Since exception was called it's safe to assume no other processing happened
        assertEquals("Your new password needs to be different from your current one!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        verify(authUtils, never()).getAuthUserId();
        verify(serviceUtils, never()).getUserById(anyLong());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(userRepository, never()).save(any(UserEntity.class));
        verify(logoutService, never()).logout(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(userMapper, never()).toDTO(any(UserEntity.class));
    }

    @Test
    public void testChangePasswordWhenBadPassword() {
        // Arrange
        UserEntity authUser = TestUtil.createSavedUserA();
        String currentPassword = "CurrentPassword123";
        String newPassword = "NewPassword123";
        ChangePasswordDTO passwordDTO = ChangePasswordDTO.builder()
                .password(currentPassword)
                .newPassword(newPassword)
                .confirmNewPassword(newPassword)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(serviceUtils.getUserById(authUser.getId())).thenReturn(authUser);
        when(passwordEncoder.matches(passwordDTO.getPassword(), authUser.getPassword()))
                .thenReturn(false);

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> userService.changePassword(request, response, passwordDTO));

        // Assert and verify: Since exception was called it's safe to assume no other processing happened
        assertEquals("Password you entered is incorrect, and doesn't match your current password!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());

        // Verify that the correct functions were called
        verify(authUtils, times(1)).getAuthUserId();
        verify(serviceUtils, times(1)).getUserById(authUser.getId());
        verify(passwordEncoder, times(1)).matches(passwordDTO.getPassword(), authUser.getPassword());

        verify(userRepository, never()).save(authUser);
        verify(logoutService, never()).logout(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(userMapper, never()).toDTO(authUser);
    }

    @Test
    public void testChangePasswordWhenSuccess() {
        // Arrange, expectedDTO should be fine since password information is never included
        UserEntity authUser = TestUtil.createSavedUserA();
        UserDTO expectedDTO = TestUtil.createUserDTOA();

        String currentPassword = "CurrentPassword123";
        String newPassword = "NewPassword123";
        ChangePasswordDTO passwordDTO = ChangePasswordDTO.builder()
                .password(currentPassword)
                .newPassword(newPassword)
                .confirmNewPassword(newPassword)
                .build();

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUser.getId());
        when(serviceUtils.getUserById(authUser.getId())).thenReturn(authUser);
        // Simulate a good password
        when(passwordEncoder.matches(passwordDTO.getPassword(), authUser.getPassword()))
                .thenReturn(true);
        when(userMapper.toDTO(authUser)).thenReturn(expectedDTO);

        // Act
        UserDTO resultDTO = userService.changePassword(
                request,
                response,
                passwordDTO
        );

        // Assert and verify
        assertEquals(expectedDTO, resultDTO);
        verify(authUtils, times(1)).getAuthUserId();
        verify(serviceUtils, times(1)).getUserById(authUser.getId());
        verify(userRepository, times(1)).save(authUser);
        verify(logoutService, times(1)).logout(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(userMapper, times(1)).toDTO(authUser);
    }
}

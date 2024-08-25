package com.knguyendev.api.services.impl;

import com.knguyendev.api.domain.dto.User.*;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.enumeration.UserRole;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.mappers.UserMapper;
import com.knguyendev.api.repositories.TaskListRepository;
import com.knguyendev.api.repositories.TaskRepository;
import com.knguyendev.api.repositories.UserRelationshipRepository;
import com.knguyendev.api.repositories.UserRepository;
import com.knguyendev.api.services.LogoutService;
import com.knguyendev.api.services.UserService;
import com.knguyendev.api.utils.AuthUtils;
import com.knguyendev.api.utils.ServiceUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Create a class that implements the UserService interface. So this class actually contains the implementation and code
 * for the methods and whatnot.
 * <p>
 *
 * NOTE: Now you need to include the deletion of UserRelationships when a User themselves is deleted
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final TaskListRepository taskListRepository;
    private final TaskRepository taskRepository;

    private final UserRelationshipRepository userRelationshipRepository;
    private final UserMapper userMapper;
    private final LogoutService logoutService;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtils authUtils;
    private final ServiceUtils serviceUtils;

    public UserServiceImpl(UserRepository userRepository, TaskListRepository taskListRepository, TaskRepository taskRepository, UserRelationshipRepository userRelationshipRepository, LogoutService logoutService, UserMapper userMapper, PasswordEncoder passwordEncoder, AuthUtils authUtils, ServiceUtils serviceUtils) {
        this.userRepository = userRepository;
        this.taskListRepository = taskListRepository;
        this.taskRepository = taskRepository;
        this.userRelationshipRepository = userRelationshipRepository;
        this.logoutService = logoutService;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authUtils = authUtils;
        this.serviceUtils = serviceUtils;
    }

    /**
     * Handles all pieces of logic involving the deletion of a user
     * <p>
     * NOTE:
     * Applying @Transactional directly to the method handling the high-level logic (such as deleteById) ensures that the entire operation, including the calls to inner methods (like deleteUserAndAssociatedData), is executed within a single transaction.
     * <p>
     * If @Transactional is placed only on the inner method (deleteUserAndAssociatedData), it may not handle transactions as expected because the calling method (if not transactional) won't support proper transaction management.
     * @param userId ID of the user being deleted
     */
    private void deleteUserAndAssociatedData(Long userId) {
        // Delete the user
        userRepository.deleteById(userId);

        // Delete their relationships
        userRelationshipRepository.deleteByUserId(userId);

        // Delete the tasks first
        taskRepository.deleteByUserId(userId);

        // Delete then delete the task lists
        taskListRepository.deleteByUserId(userId);
    }

    @Override
    public UserDTO findById(Long id)  {
        UserEntity user = serviceUtils.getUserById(id);
        return userMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> findAll() {
        List<UserEntity> users = StreamSupport.stream(userRepository.findAll().spliterator(), false).toList();
        return users.stream().map(userMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO getAuthenticatedUser() {
        Long userId = authUtils.getAuthUserId();
        UserEntity user = serviceUtils.getUserById(userId);
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO updateAccountProfile(UserProfileUpdateDTO userProfileUpdateDTO) {
        Long userId = authUtils.getAuthUserId();

        UserEntity user = serviceUtils.getUserById(userId);

        boolean isDiff = false;
        /*
         * NOTE: Then in each one if these, you can apply your own extra business logic. Like for example, maybe
         * if you had extra logic when updating the email, then you could do it in the email clause. Instead of updating the 'email'
         * field, you could update emailToVerify, create and send an email verification token (probably call another function)
         * and do much more. This is just a basic example, but I'm choosing this approach over combining the query. I'm
         * choosing code modularity and cleanliness over a slight performance gain of doing one db query. Also, it's not
         * likely that the user updates both their username and email during a profile update.
         */
        if (!userProfileUpdateDTO.getUsername().equals(user.getUsername())) {
            Optional<UserEntity> existingUser = userRepository.findByUsername(userProfileUpdateDTO.getUsername());
            if (existingUser.isPresent()) {
                throw new ServiceException("Username '" + userProfileUpdateDTO.getUsername() + "' is already taken!", HttpStatus.BAD_REQUEST);
            }
            user.setUsername(userProfileUpdateDTO.getUsername());
            isDiff = true;
        }

        if (!userProfileUpdateDTO.getEmail().equals(user.getEmail())) {
            Optional<UserEntity> existingUser = userRepository.findByEmail(userProfileUpdateDTO.getEmail());
            if (existingUser.isPresent()) {
                throw new ServiceException("Email '" + userProfileUpdateDTO.getEmail() + "' is already in use!", HttpStatus.BAD_REQUEST);
            }
            user.setEmail(userProfileUpdateDTO.getEmail());
            isDiff = true;
        }

        if (!userProfileUpdateDTO.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(userProfileUpdateDTO.getFirstName());
            isDiff = true;
        }

        if (!userProfileUpdateDTO.getLastName().equals(user.getLastName())) {
            user.setLastName(userProfileUpdateDTO.getLastName());
            isDiff = true;
        }

        if (!userProfileUpdateDTO.getBiography().equals(user.getBiography())) {
            user.setBiography(userProfileUpdateDTO.getBiography());
            isDiff = true;
        }

        // If the update applied new data changes, update the user in the database
        if (isDiff) {
            userRepository.save(user);
        }
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public UserDTO deleteAccount(HttpServletRequest request, HttpServletResponse response, UserDeleteDTO userDeleteDTO) {
        Long userId = authUtils.getAuthUserId();
        UserEntity user = serviceUtils.getUserById(userId);

        // if they're an administrator, prevent them from deleting their own account
        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SUPER_ADMIN) {
            throw new ServiceException(
                    "Admins aren't allowed to delete their own accounts! Please get a 'Super Admin' to delete it for you.",
                    HttpStatus.FORBIDDEN
            );
        }

        // Verify that the password entered is correct
        if (!passwordEncoder.matches(userDeleteDTO.getPassword(), user.getPassword())) {
            throw new ServiceException("Password you entered is incorrect, and doesn't match your current password!", HttpStatus.BAD_REQUEST);
        }

        deleteUserAndAssociatedData(userId);

        logoutService.logout(request, response);
        return userMapper.toDTO(user);
    }


    @Override
    @Transactional
    public UserDTO deleteById(Long id) throws ServiceException {
        // Prevent the user from deleting their own account; operations like that should be done by deleteAccount() method
        Long authUserId = authUtils.getAuthUserId();
        if (authUserId.equals(id)) {
            throw new ServiceException("You can't delete your own account!", HttpStatus.FORBIDDEN);
        }

        // Get the target user being deleted.
        UserEntity user = serviceUtils.getUserById(id);

        // If the user being deleted is a 'Super Admin', then stop that request immediately
        if (user.getRole() == UserRole.SUPER_ADMIN) {
            throw new ServiceException("User is a 'Super Admin'. They cannot be deleted!", HttpStatus.FORBIDDEN);
        }

        /*
         * If an admin is being deleted, we need to check if the requester/authenticated user is a super admin. If they
         * aren't then we'll deny the request
         */
        if (user.getRole() == UserRole.ADMIN) {
            UserEntity authUser = serviceUtils.getUserById(authUserId);
            if (authUser.getRole() != UserRole.SUPER_ADMIN) {
                throw new ServiceException("You must be a 'Super Admin' to be able to delete another admin!", HttpStatus.FORBIDDEN);
            }
        }

        // Delete the user and any corresponding relationships
        deleteUserAndAssociatedData(user.getId());

        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO changePassword(HttpServletRequest request, HttpServletResponse response, ChangePasswordDTO changePasswordDTO) {
        // If the current password they entered is the same as teh new one, then throw an error saying that they need to be different.
        if (changePasswordDTO.getPassword().equals(changePasswordDTO.getNewPassword())) {
            throw new ServiceException("Your new password needs to be different from your current one!", HttpStatus.BAD_REQUEST);
        }

        // Get the ID of the authenticated user and fetch the user from the database
        Long userId = authUtils.getAuthUserId();
        UserEntity user = serviceUtils.getUserById(userId);

        // Check that the passwords hash; ensure the user entered their correct current password
        if (!passwordEncoder.matches(changePasswordDTO.getPassword(), user.getPassword())) {
            throw new ServiceException("Password you entered is incorrect, and doesn't match your current password!", HttpStatus.BAD_REQUEST);
        }

        // Apply new password and ensure it's hashed, then save it into the database
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepository.save(user);

        // Do a logout on the authenticated user
        logoutService.logout(request, response);
        return userMapper.toDTO(user);
    }
}
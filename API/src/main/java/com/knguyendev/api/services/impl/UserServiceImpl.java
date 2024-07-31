package com.knguyendev.api.services.impl;


import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.enumeration.UserRole;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.mappers.UserMapper;
import com.knguyendev.api.repositories.UserRepository;
import com.knguyendev.api.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Create a class that implements the UserService interface. So this class actually contains the implementation and code
 * for the methods and whatnot.
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
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
        return userMapper.toDTO(userRepository.save(newUser));
    }

    @Override
    public UserDTO findByUsername(String username) throws ServiceException {
        Optional<UserEntity> result = userRepository.findByUsername(username);
        if (result.isEmpty()) {
            throw new ServiceException("A user with the username '" + username + "' wasn't found!", HttpStatus.NOT_FOUND);
        }
        return userMapper.toDTO(result.get());
    }

    // Deleting
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }
}

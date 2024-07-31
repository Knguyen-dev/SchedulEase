package com.knguyendev.api.controllers;

import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.mappers.Mapper;
import com.knguyendev.api.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Spring Security by default will secure every server route behind a username and password form.
 * However, for our purposes, we don't want to guard our routes behind a username and password form.
 * Is it normal and cool if we disable this because I want this to act as a normal API?
 */
@RestController
@RequestMapping("/auth") // prefix '/auth' to all the routes, so now the registration is '/auth/register'
public class AuthController {

    // Setting up to receive beans for UserService and Mapper<UserEntity, UserDto>
    private UserService userService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }


    /**
     * Route for registering a user.
     *
     * + How validation works:
     * 1. '@Valid' triggers the validation on the UserRegistrationDTO object
     * 2. If validation finds something wrong a 'MethodArgumentNotValidException' is thrown since we're
     *    validating a function parameter, 'UserRegistrationDTO'.
     *
     *
     * @param userRegistrationDTO - Object containing user registration input
     * @return The created user
     */
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {

        // normalize the registration data;
        userRegistrationDTO.normalizeData();

        // Attempt to register the user using the UserService!

        // Input has been validated to meet constraints, but we need to
        // Efficient validate that there are no other users with either the username or email.
        // However we must ensure that is done in the service layer. Also be sure to lowercase username, email, and trim stuff of whitespace on the outside
        return new ResponseEntity<>("good", HttpStatus.CREATED);
    }
}
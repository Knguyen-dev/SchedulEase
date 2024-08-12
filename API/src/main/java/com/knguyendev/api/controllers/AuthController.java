package com.knguyendev.api.controllers;

import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.User.UserLoginDTO;
import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.enumeration.UserRole;
import com.knguyendev.api.services.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring Security by default will secure every server route behind a username and password form.
 * However, for our purposes, we don't want to guard our routes behind a username and password form.
 * Is it normal and cool if we disable this because I want this to act as a normal API?
 */
@RestController
@RequestMapping("/api/v1/auth") // prefix '/auth' to all the routes, so now the registration is '/auth/register'
public class AuthController {

    // Setting up to receive beans for UserService and Mapper<UserEntity, UserDto>
    private final AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    /**
     * Route for registering a user.
     * @param userRegistrationDTO - Object containing user registration input
     * @return The created user
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        // normalize the registration data;
        userRegistrationDTO.normalizeData();

        // Attempt to register the user using the UserService!
        UserDTO userDTO = authService.registerUser(userRegistrationDTO, UserRole.USER);

        // Return the response DTO
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@Valid @RequestBody UserLoginDTO userLoginDTO, HttpServletRequest request, HttpServletResponse response) {

        userLoginDTO.normalizeData();

        UserDTO userDTO = authService.loginUser(userLoginDTO, request, response);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
     }



    @GetMapping("/session")
    public ResponseEntity<Map<String, Object>> printSessionAttributes(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // Session details
        response.put("Session ID", session.getId());
        response.put("Creation Time", session.getCreationTime());
        response.put("Last Accessed Time", session.getLastAccessedTime());
        response.put("Max Inactive Interval", session.getMaxInactiveInterval());

        // Session attributes
        Map<String, Object> attributes = new HashMap<>();
        session.getAttributeNames().asIterator().forEachRemaining(attributeName -> attributes.put(attributeName, session.getAttribute(attributeName)));
        response.put("Session Attributes", attributes);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
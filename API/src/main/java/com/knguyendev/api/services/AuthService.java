package com.knguyendev.api.services;

import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.User.UserLoginDTO;
import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.enumeration.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {


    /**
     * Service function used to create a new user in the database
     *
     * @param userRegistrationDTO Data that user inputted in order to create an account. Data is assumed to be normalized and validated for constraints before being passed into this function.
     * @param role The role that the user registered is being registered for.
     * @return Will return a UserEntity of the user whose account has been created.
     */
    UserDTO registerUser(UserRegistrationDTO userRegistrationDTO, UserRole role);

    /**
     * Method used to log in and authenticate an existing user in the database
     * @param userLoginDTO Object containing the credential information needed to log a user
     * @param request The Http request object itself. Used to either access an existing session or create a new one if the user is
     *                           being authenticated for the first time.
     * @param response  We'll need to modify the response and do things such as setting a session cookie
     */
     UserDTO loginUser(UserLoginDTO userLoginDTO, HttpServletRequest request, HttpServletResponse response);
}

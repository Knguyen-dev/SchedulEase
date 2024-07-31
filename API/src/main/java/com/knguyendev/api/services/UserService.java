package com.knguyendev.api.services;


import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.User.UserLoginDTO;
import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.enumeration.UserRole;

import java.util.List;
import java.util.Optional;
/**
 * Let's define an interface for our UserService. With this you can define some
 * service or business-logic related functions (relating to the persistence layer anyways).
 * Then all of your implementations need to align with this interface. This makes it easy
 * to organize things and abstract logic away.
 *
 *
 */
public interface UserService {

    /**
     * Service function used to create a new user in the database
     *
     * @param userRegistrationDTO Data that user inputted in order to create an account. Data is assumed to be normalized and validated for constraints before being passed into this function.
     * @param role The role that the user registered is being registered for.
     * @return Will return a UserEntity of the user whose account has been created.
     */
    UserDTO registerUser(UserRegistrationDTO userRegistrationDTO, UserRole role);

    // finding/reading
    UserDTO findByUsername(String username);


    // UserDTO loginUser(UserLoginDTO userLoginDTO);



    void deleteById(Long id);
    // updating; A service function to update a user's profile

    // UserDTO updateProfile();


}

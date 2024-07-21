package com.knguyendev.TravelApp.services;


import com.knguyendev.TravelApp.domain.dto.user.UserRegistrationDTO;
import com.knguyendev.TravelApp.domain.entities.UserEntity;

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


    UserEntity save(UserEntity userEntity);
    Optional<UserEntity> findByUsername(String username);
    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUsernameOrEmail(String username, String email);


    void deleteById(Long id);

    /**
     * Attempts to fetch a user by their email and plaintext password
     *
     * @param email - Email of the user
     * @param password - A plaintext version of their password
     * @return Optional<UserEntity>
     */
    Optional<UserEntity> findByEmailAndPassword(String email, String password);


    List<UserEntity> findAll();


    UserEntity registerUser(UserRegistrationDTO userRegistrationDTO);



}

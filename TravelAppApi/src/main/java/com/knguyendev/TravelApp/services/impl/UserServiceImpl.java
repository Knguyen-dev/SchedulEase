package com.knguyendev.TravelApp.services.impl;


import com.knguyendev.TravelApp.domain.dto.user.UserRegistrationDTO;
import com.knguyendev.TravelApp.domain.entities.UserEntity;
import com.knguyendev.TravelApp.repositories.UserRepository;
import com.knguyendev.TravelApp.services.UserService;
import org.springframework.stereotype.Service;

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

    // Set up our private properties and accept beans from Spring Context
    private final UserRepository userRepository;
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Persists a UserEntity into the database.
     *
     * @param userEntity - A user entity
     * @return UserEntity
     */
    @Override
    public UserEntity save(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<UserEntity> findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public List<UserEntity> findAll() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    /**
     * @param userRegistrationDTO DTO containing all of the data needed to register a new user. Note that data was only been validated
     *                            to meet basic constraints. We still need to transform the data such as lowercasing username
     *                            and email. Also trimming the whitespace off of the data! As well as this we need to do
     *                            checks to see if our lower-cased username or email correspond with an already existing user.
     * @return Will return a UserEntity
     */
    @Override
    public UserEntity registerUser(UserRegistrationDTO userRegistrationDTO) {






        // Check if


        return null;
    }

    public Optional<UserEntity> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }


    /**
     *
     *
     * @param username - Username of the account we're attempting to create. This hasn't be lowercased yet, so we'll lowercase it in this function
     * @param firstName -
     * @param lastName -
     * @param biography -
     * @param email -
     * @param password -
     * @return Would either throw an error or returns a UserEntity
     */

//    public Optional<UserEntity> registerUser(String username, String firstName, String lastName, String biography, String email, String password) {



        // If username already taken, we gotta throw an error (4xx)

        // If email already taken, we also gotta throw an error (4xx)

        // Create a user entity now and return it
//    }


}

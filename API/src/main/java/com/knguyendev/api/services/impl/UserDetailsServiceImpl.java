package com.knguyendev.api.services.impl;

import com.knguyendev.api.domain.dto.User.UserDetailsImpl;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;


/**
 * Service class that's used to get a user's details from the database
 */
@Service(value="customUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Used by Spring Security to retrieve a user's information from a data source (databasein this case) based on their
     * username. It's typically used in the 'AuthenticationProvider' to validate a user's credential's.
     *
     * @param username Username of the user
     * @return Returns a 'UserDetails' instance representing the User.
     * @throws UsernameNotFoundException An exception thrown when the username didn't correlate to a User in the database.
     * This inherits from 'AuthenticationException'.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> result = userRepository.findByUsername(username);
        if (result.isEmpty()) {
            throw new UsernameNotFoundException("User with username '" + username + "' wasn't found!");
        }
        return new UserDetailsImpl(result.get());
    }
}

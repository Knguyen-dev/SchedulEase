package com.knguyendev.api.utils;

import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public class ServiceUtils {
    private final UserRepository userRepository;
    public ServiceUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * Retrieves a UserEntity by its ID. If no user was found it will throw an error
     *
     * @param id The ID of the user to be retrieved.
     * @return The UserEntity associated with the given ID.
     * @throws ServiceException If no user is found with the given ID.
     *         This exception is thrown with a {@link HttpStatus#NOT_FOUND} status to indicate that the user could not be located in the database.
     * <p>
     * This method uses the {@link UserRepository} to find the user by the provided ID. If the user exists, the method returns the
     * corresponding {@link UserEntity} object. If the user does not exist, it throws a {@link ServiceException} with a 404 status code,
     * indicating that the user was not found. This ensures that the calling method can handle the case where the user does not exist in
     * a consistent manner.
     * </p>
     */
    public UserEntity getUserById(Long id) throws ServiceException {
        Optional<UserEntity> result = userRepository.findById(id);
        if (result.isEmpty()) {
            throw new ServiceException("User with ID '" + id + "' not found!", HttpStatus.NOT_FOUND);
        }
        return result.get();
    }
}
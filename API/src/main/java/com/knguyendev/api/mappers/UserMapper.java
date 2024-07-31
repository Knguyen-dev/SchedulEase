package com.knguyendev.api.mappers;

import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.domain.entities.UserEntity;

public interface UserMapper {

    /**
     * Converts a user's registration info into a UserEntity
     *
     * @param userRegistrationDTO A DTO containing information needed to register a user
     * @return An entity representing the user signing up
     */
    UserEntity toEntity(UserRegistrationDTO userRegistrationDTO);


    /**
     * Returns a user entity back into a userDTO for server responses
     *
     * @param entity A user entity
     * @return A DTO made for responding to API calls for users.
     */
    UserDTO toDTO(UserEntity entity);


}

package com.knguyendev.api.mappers.impl;

import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.mappers.UserMapper;
import org.springframework.stereotype.Component;


/**
 * A UserMapper that implements our Mapper interface. This does the mapping for the
 * UserEntity and UserDto classes.
 * <p>
 * NOTE: We declared default values for the UserEntity, the isVerified, is false by default. If you use the builder pattern,
 * then that value isn't going to be initialized, so use the
 */
@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public UserEntity toEntity(UserRegistrationDTO userRegistrationDTO) {
        UserEntity user = new UserEntity();
        user.setUsername(userRegistrationDTO.getUsername());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setFirstName(userRegistrationDTO.getFirstName());
        user.setLastName(userRegistrationDTO.getLastName());
        user.setPassword(userRegistrationDTO.getPassword());
        return user;
    }

    @Override
    public UserDTO toDTO(UserEntity userEntity) {
        return UserDTO.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName())
                .biography(userEntity.getBiography())
                .isVerified(userEntity.getIsVerified())
                .createdAt(userEntity.getCreatedAt())
                .role(userEntity.getRole())
                .build();
    }
}

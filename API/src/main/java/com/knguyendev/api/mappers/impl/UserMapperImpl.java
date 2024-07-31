package com.knguyendev.api.mappers.impl;

import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.mappers.UserMapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


/**
 * A UserMapper that implements our Mapper interface. This does the mapping for the
 * UserEntity and UserDto classes.
 */
@Component
public class UserMapperImpl implements UserMapper {

    // ModelMapper bean should be created in our config package
    private final ModelMapper modelMapper;
    public UserMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserEntity toEntity(UserRegistrationDTO userRegistrationDTO) {
        return modelMapper.map(userRegistrationDTO, UserEntity.class);
    }

    @Override
    public UserDTO toDTO(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDTO.class);
    }
}

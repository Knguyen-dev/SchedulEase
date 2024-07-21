package com.knguyendev.TravelApp.mappers.impl;


import com.knguyendev.TravelApp.domain.dto.user.UserDTO;
import com.knguyendev.TravelApp.domain.entities.UserEntity;
import com.knguyendev.TravelApp.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


/**
 * A UserMapper that implements our Mapper interface. This does the mapping for the
 * UserEntity and UserDto classes.
 */
@Component
public class UserMapperImpl implements Mapper<UserEntity, UserDTO> {

    // ModelMapper bean should be created in our config package
    private ModelMapper modelMapper;
    public UserMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDTO mapTo(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserDTO.class);
    }

    @Override
    public UserEntity mapFrom(UserDTO userDto) {
        return modelMapper.map(userDto, UserEntity.class);
    }
}

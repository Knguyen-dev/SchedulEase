package com.knguyendev.api.mappers.impl;


import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.UserRelationship.UserRelationshipDTO;
import com.knguyendev.api.domain.entities.UserRelationshipEntity;
import com.knguyendev.api.mappers.UserMapper;
import com.knguyendev.api.mappers.UserRelationshipMapper;
import org.springframework.stereotype.Component;

@Component
public class UserRelationshipMapperImpl implements UserRelationshipMapper {

    private final UserMapper userMapper;
    public UserRelationshipMapperImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserRelationshipDTO toDTO(UserRelationshipEntity entity) {
        UserDTO firstUserDTO = userMapper.toDTO(entity.getFirstUser());
        UserDTO secondUserDTO = userMapper.toDTO(entity.getSecondUser());
        return UserRelationshipDTO.builder()
                .firstUser(firstUserDTO)
                .secondUser(secondUserDTO)
                .status(entity.getStatus())
                .build();
    }
}

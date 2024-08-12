package com.knguyendev.api.mappers;

import com.knguyendev.api.domain.dto.UserRelationship.UserRelationshipDTO;
import com.knguyendev.api.domain.entities.UserRelationshipEntity;

public interface UserRelationshipMapper {
    UserRelationshipDTO toDTO(UserRelationshipEntity entity);
}

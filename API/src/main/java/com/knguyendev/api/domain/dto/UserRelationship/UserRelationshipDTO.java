package com.knguyendev.api.domain.dto.UserRelationship;

import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.entities.UserRelationshipEntity;
import com.knguyendev.api.enumeration.UserRelationshipStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRelationshipDTO {
    UserDTO firstUser;
    UserDTO secondUser;
    UserRelationshipStatus status;
}

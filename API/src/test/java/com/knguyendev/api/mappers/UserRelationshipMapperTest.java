//package com.knguyendev.api.mappers;
//
//
//import com.knguyendev.api.TestUtil;
//import com.knguyendev.api.domain.dto.User.UserDTO;
//import com.knguyendev.api.domain.dto.UserRelationship.UserRelationshipDTO;
//import com.knguyendev.api.domain.entities.UserEntity;
//import com.knguyendev.api.domain.entities.UserRelationshipEntity;
//import com.knguyendev.api.enumeration.UserRelationshipStatus;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//public class UserRelationshipMapperTest {
//    private final UserRelationshipMapper userRelationshipMapper;
//
//    @Autowired
//    public UserRelationshipMapperTest(UserRelationshipMapper userRelationshipMapper) {
//        this.userRelationshipMapper = userRelationshipMapper;
//    }
//
//    @Test
//    void testEntityMapsToDTO() {
//        UserEntity firstUser = TestUtil.createSavedUserA();
//        UserEntity secondUser = TestUtil.createSavedUserB();
//        firstUser.setId(1L);
//        secondUser.setId(2L);
//        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
//                .id(1L)
//                .firstUser(firstUser)
//                .secondUser(secondUser)
//                .status(UserRelationshipStatus.FRIENDS)
//                .build();
//        UserRelationshipDTO relationshipDTO = userRelationshipMapper.toDTO(relationship);
//        UserDTO firstUserDTO = relationshipDTO.getFirstUser();
//        UserDTO secondUserDTO = relationshipDTO.getSecondUser();
//
//        // Assert relationship info matches
//        assertEquals(relationship.getStatus(), relationshipDTO.getStatus());
//
//        // Assert that first and second user are good
//        assertEquals(firstUser.getId(), firstUserDTO.getId());
//        assertEquals(secondUser.getId(), secondUserDTO.getId());
//    }
//}

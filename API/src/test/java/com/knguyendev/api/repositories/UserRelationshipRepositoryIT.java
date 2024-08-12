package com.knguyendev.api.repositories;


import com.knguyendev.api.TestUtil;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.domain.entities.UserRelationshipEntity;
import com.knguyendev.api.enumeration.UserRelationshipStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRelationshipRepositoryIT {
    private final UserRelationshipRepository underTest;
    private final UserRepository userRepository;

    @Autowired
    public UserRelationshipRepositoryIT(UserRepository userRepository, UserRelationshipRepository underTest) {
        this.userRepository = userRepository;
        this.underTest = underTest;
    }

    @Test
    public void testThatRelationshipCanBeCreatedAndFound() {
        UserEntity userA = TestUtil.createSavedUserA();
        UserEntity userB = TestUtil.createSavedUserB();

        userRepository.save(userA);
        userRepository.save(userB);

        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .firstUser(userA)
                .secondUser(userB)
                .status(UserRelationshipStatus.FRIENDS)
                .build();
        underTest.save(relationship);

        Optional<UserRelationshipEntity> result = underTest.findById(relationship.getId());
        assertThat(result).isPresent();
        UserRelationshipEntity fetchedRelationship = result.get();
        assertThat(fetchedRelationship.getFirstUser().getId()).isEqualTo(userA.getId());
        assertThat(fetchedRelationship.getSecondUser().getId()).isEqualTo(userB.getId());
        assertThat(fetchedRelationship.getStatus()).isEqualTo(relationship.getStatus());
    }

    @Test
    public void testThatManyRelationshipsCanBeCreatedAndFound() {
        UserEntity userA = TestUtil.createSavedUserA();
        UserEntity userB = TestUtil.createSavedUserB();
        UserEntity userC = TestUtil.createSavedUserC();
        UserEntity userD = TestUtil.createSavedUserD();

        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);
        userRepository.save(userD);

        UserRelationshipEntity relationshipA = UserRelationshipEntity.builder()
                .firstUser(userA)
                .secondUser(userB)
                .status(UserRelationshipStatus.PENDING_SECOND_FIRST)
                .build();
        UserRelationshipEntity relationshipB = UserRelationshipEntity.builder()
                .firstUser(userC)
                .secondUser(userD)
                .status(UserRelationshipStatus.BLOCK_BOTH)
                .build();
        underTest.save(relationshipA);
        underTest.save(relationshipB);

        Optional<UserRelationshipEntity> resultA = underTest.findById(relationshipA.getId());
        Optional<UserRelationshipEntity> resultB = underTest.findById(relationshipB.getId());

        assertThat(resultA).isPresent();
        assertThat(resultA.get().getFirstUser().getId()).isEqualTo(userA.getId());
        assertThat(resultA.get().getSecondUser().getId()).isEqualTo(userB.getId());
        assertThat(resultA.get().getStatus()).isEqualTo(relationshipA.getStatus());

        assertThat(resultB).isPresent();
        assertThat(resultB.get().getFirstUser().getId()).isEqualTo(userC.getId());
        assertThat(resultB.get().getSecondUser().getId()).isEqualTo(userD.getId());
        assertThat(resultB.get().getStatus()).isEqualTo(relationshipB.getStatus());

    }

    @Test
    public void testThatRelationshipCanBeDeleted() {
        UserEntity userA = TestUtil.createSavedUserA();
        UserEntity userB = TestUtil.createSavedUserB();
        userRepository.save(userA);
        userRepository.save(userB);
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(userA)
                .secondUser(userB)
                .status(UserRelationshipStatus.FRIENDS)
                .build();
        underTest.save(relationship);
        underTest.deleteById(relationship.getId());
        Optional<UserRelationshipEntity> result = underTest.findById(relationship.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testThatRelationshipCanBeUpdated() {
        UserEntity userA = TestUtil.createSavedUserA();
        UserEntity userB = TestUtil.createSavedUserB();
        userRepository.save(userA);
        userRepository.save(userB);
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(userA)
                .secondUser(userB)
                .status(UserRelationshipStatus.FRIENDS)
                .build();
        underTest.save(relationship);

        relationship.setStatus(UserRelationshipStatus.PENDING_FIRST_SECOND);
        underTest.save(relationship);

        Optional<UserRelationshipEntity> result = underTest.findById(relationship.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getFirstUser().getId()).isEqualTo(userA.getId());
        assertThat(result.get().getSecondUser().getId()).isEqualTo(userB.getId());
        assertThat(result.get().getStatus()).isEqualTo(relationship.getStatus());

    }

    @Test
    public void testFindByFirstUserIdAndSecondUserId() {
        UserEntity userA = TestUtil.createSavedUserA();
        UserEntity userB = TestUtil.createSavedUserB();
        userRepository.save(userA);
        userRepository.save(userB);
        UserRelationshipEntity relationship = UserRelationshipEntity.builder()
                .id(1L)
                .firstUser(userA)
                .secondUser(userB)
                .status(UserRelationshipStatus.FRIENDS)
                .build();
        underTest.save(relationship);

        Optional<UserRelationshipEntity> result = underTest.findByFirstUserIdAndSecondUserId(
                userA.getId(),
                userB.getId()
        );
        assertThat(result).isPresent();
        assertThat(result.get().getFirstUser().getId()).isEqualTo(userA.getId());
        assertThat(result.get().getSecondUser().getId()).isEqualTo(userB.getId());
        assertThat(result.get().getStatus()).isEqualTo(relationship.getStatus());
    }

    @Test
    public void testFindByUserId() {
        // Arrange
        UserEntity userA = TestUtil.createSavedUserA();
        UserEntity userB = TestUtil.createSavedUserB();
        UserEntity userC = TestUtil.createSavedUserC();
        UserEntity userD = TestUtil.createSavedUserD();

        userRepository.save(userA);
        userRepository.save(userB);
        userRepository.save(userC);
        userRepository.save(userD);

        // Create 3 relationships related to userA
        UserRelationshipEntity relationshipAB = UserRelationshipEntity.builder()
                .firstUser(userA)
                .secondUser(userB)
                .status(UserRelationshipStatus.FRIENDS)
                .build();
        UserRelationshipEntity relationshipAC = UserRelationshipEntity.builder()
                .firstUser(userA)
                .secondUser(userC)
                .status(UserRelationshipStatus.PENDING_FIRST_SECOND)
                .build();
        UserRelationshipEntity relationshipAD = UserRelationshipEntity.builder()
                .firstUser(userA)
                .secondUser(userD)
                .status(UserRelationshipStatus.BLOCK_BOTH)
                .build();
        underTest.save(relationshipAB);
        underTest.save(relationshipAC);
        underTest.save(relationshipAD);

        // Act
        List<UserRelationshipEntity> relationships = underTest.findByUserId(userA.getId());

        // Assert
        assertThat(relationships.size()).isEqualTo(3);
        assertThat(relationships).containsExactlyInAnyOrder(relationshipAB, relationshipAC, relationshipAD);
    }
}

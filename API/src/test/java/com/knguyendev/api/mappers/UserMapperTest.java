package com.knguyendev.api.mappers;
import com.knguyendev.api.TestUtil;
import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserMapperTest {
    private final UserMapper userMapper;

    @Autowired
    public UserMapperTest(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Test
    void testRegistrationDTOMapsToEntity() {

        UserRegistrationDTO userRegistration = TestUtil.createUserRegistrationDTOA();

        // Then: Assert all the fields from the UserRegistrationDTO have mapped correctly to the UserEntity
        UserEntity user = userMapper.toEntity(userRegistration);

        // Assert all the fields from the UserRegistrationDTO have mapped over to the UserEntity correctly.
        assertThat(userRegistration.getUsername()).isEqualTo(user.getUsername());
        assertThat(userRegistration.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(userRegistration.getLastName()).isEqualTo(user.getLastName());
        assertThat(userRegistration.getEmail()).isEqualTo(user.getEmail());
        assertThat(userRegistration.getPassword()).isEqualTo(user.getPassword());

        // Assert the default values or unassigned fields
        assertThat(user.getIsVerified()).isFalse();  // `isVerified` should default to `false`
        assertThat(user.getRole()).isNull();         // Role should be null if not set during mapping

        // Ensure any other fields aren't filled in: id, biography, emailToVerify, verifyEmailToken, verifyEmailTokenExpires, password, passwordResetToken, passwordResetTokenExpires, createdAt
        assertThat(user.getId()).isNull();
        assertThat(user.getBiography()).isEqualTo("");
        assertThat(user.getEmailToVerify()).isNull();
        assertThat(user.getVerifyEmailToken()).isNull();
        assertThat(user.getVerifyEmailTokenExpires()).isNull();
        assertThat(user.getPasswordResetToken()).isNull();
        assertThat(user.getPasswordResetTokenExpires()).isNull();
        assertThat(user.getCreatedAt()).isNull();
    }

    @Test
    void testEntityMapsToDTO() {
        UserEntity userA = TestUtil.createSavedUserA();
        UserDTO userDTO = userMapper.toDTO(userA);

        // Assertions
        assertThat(userDTO.getId()).isEqualTo(userA.getId());
        assertThat(userDTO.getUsername()).isEqualTo(userA.getUsername());
        assertThat(userDTO.getFirstName()).isEqualTo(userA.getFirstName());
        assertThat(userDTO.getLastName()).isEqualTo(userA.getLastName());
        assertThat(userDTO.getBiography()).isEqualTo(userA.getBiography());
        assertThat(userDTO.getEmail()).isEqualTo(userA.getEmail());
        assertThat(userDTO.getIsVerified()).isEqualTo(userA.getIsVerified());
        assertThat(userDTO.getCreatedAt()).isEqualTo(userA.getCreatedAt());
    }
}

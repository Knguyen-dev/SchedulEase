package com.knguyendev.api;

import com.knguyendev.api.domain.dto.ItemColor.ItemColorCreateDTO;
import com.knguyendev.api.domain.dto.ItemColor.ItemColorDTO;
import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.User.UserDetailsImpl;
import com.knguyendev.api.domain.dto.User.UserProfileUpdateDTO;
import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.domain.dto.UserRelationship.UserRelationshipDTO;
import com.knguyendev.api.domain.entities.ItemColorEntity;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.domain.entities.UserRelationshipEntity;
import com.knguyendev.api.enumeration.UserRelationshipStatus;
import com.knguyendev.api.enumeration.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.ZoneId;
import java.time.ZonedDateTime;


public class TestUtil {

    // Constants for 'User A'
    private static final Long USER_A_ID = 1L;
    private static final String USER_A_USERNAME = "AbJacks0n";
    private static final String USER_A_EMAIL = "AbJackson@gmail.com";
    private static final String USER_A_FIRST_NAME = "Abigail";
    private static final String USER_A_LAST_NAME = "Jackson";
    private static final String USER_A_BIOGRAPHY = "Hi I'm a content creator on Youtube and sometimes I stream on Twitch";
    private static final Boolean USER_A_IS_VERIFIED = true;
    private static final String USER_A_PASSWORD = "P$ssword_123";
    private static final ZonedDateTime USER_A_CREATED_AT = ZonedDateTime.now(ZoneId.of("UTC"));
    private static final UserRole USER_A_ROLE = UserRole.USER;

    // Constants for "User B"
    private static final Long USER_B_ID = 2L;
    private static final String USER_B_USERNAME = "JaneDoe";
    private static final String USER_B_EMAIL = "JaneDoe@example.com";
    private static final String USER_B_FIRST_NAME = "Jane";
    private static final String USER_B_LAST_NAME = "Doe";
    private static final String USER_B_BIOGRAPHY = "Software engineer who loves to explore new technologies.";
    private static final Boolean USER_B_IS_VERIFIED = false;
    private static final String USER_B_PASSWORD = "AnotherSecurePass123"; // plaintext
    private static final ZonedDateTime USER_B_CREATED_AT = ZonedDateTime.now(ZoneId.of("UTC"));
    private static final UserRole USER_B_ROLE = UserRole.ADMIN;

    // Constants for "User C"
    private static final Long USER_C_ID = 3L;
    private static final String USER_C_USERNAME = "SamSmith";
    private static final String USER_C_EMAIL = "SamSmith@example.com";
    private static final String USER_C_FIRST_NAME = "Sam";
    private static final String USER_C_LAST_NAME = "Smith";
    private static final String USER_C_BIOGRAPHY = "Aspiring writer and avid reader.";
    private static final Boolean USER_C_IS_VERIFIED = true;
    private static final String USER_C_PASSWORD = "C0mpl3xP@ssw0rd!"; // plaintext
    private static final ZonedDateTime USER_C_CREATED_AT = ZonedDateTime.now(ZoneId.of("UTC"));
    private static final UserRole USER_C_ROLE = UserRole.USER;

    // Constants for "User D"
    private static final Long USER_D_ID = 4L;
    private static final String USER_D_USERNAME = "JohnSmith";
    private static final String USER_D_EMAIL = "JohnSmith@example.com";
    private static final String USER_D_FIRST_NAME = "John";
    private static final String USER_D_LAST_NAME = "Smith";
    private static final String USER_D_BIOGRAPHY = "Tech enthusiast and blogger.";
    private static final Boolean USER_D_IS_VERIFIED = false;
    private static final String USER_D_PASSWORD = "Y3t@noth3rP@ss"; // plaintext
    private static final ZonedDateTime USER_D_CREATED_AT = ZonedDateTime.now(ZoneId.of("UTC"));
    private static final UserRole USER_D_ROLE = UserRole.USER;

    // Constants for "ItemColor A"
    private static final String ITEM_COLOR_A_NAME = "Sky Blue";
    private static final String ITEM_COLOR_A_HEXCODE = "#87CEEB";
    private static final Long ITEM_COLOR_A_ID = 1L;

    // Constants for "ItemColor B"
    private static final String ITEM_COLOR_B_NAME = "Coral";
    private static final String ITEM_COLOR_B_HEXCODE = "#FF7F50";
    private static final Long ITEM_COLOR_B_ID = 2L;



    private TestUtil() {}



    // ***** TestUtil methods for UserEntity and its related DTOs.
    public static UserEntity createSavedUserEntity(Long id, String username, String email, String firstName, String lastName, String biography, boolean isVerified, String password, ZonedDateTime createdAt, UserRole role) {
        return UserEntity.builder()
                .id(id)
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .biography(biography)
                .isVerified(isVerified)
                .password(password)
                .createdAt(createdAt)
                .role(role)
                .build();
    }

    public static UserProfileUpdateDTO createUserProfileUpdateDTO(String username, String email, String firstName, String lastName, String biography) {
        return UserProfileUpdateDTO.builder()
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .biography(biography)
                .build();
    }

    public static UserDetailsImpl createUserDetails(UserEntity user) {
        return new UserDetailsImpl(user);
    }

    public static UserRegistrationDTO createUserRegistrationDTO(String username, String email, String firstName, String lastName, String password) {
        return UserRegistrationDTO.builder()
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .password(password)
                .build();
    }

    public static UserDTO createUserDTO(Long id, String username, String email, String firstName, String lastName, String biography, Boolean isVerified, ZonedDateTime createdAt, UserRole role) {
        return UserDTO.builder()
                .id(id)
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .biography(biography)
                .isVerified(isVerified)
                .createdAt(createdAt)
                .role(role)
                .build();
    }

    public static UserEntity createSavedUserA() {
        String encryptedPassword = new BCryptPasswordEncoder().encode(USER_A_PASSWORD);
        return createSavedUserEntity(
                USER_A_ID,
                USER_A_USERNAME,
                USER_A_EMAIL,
                USER_A_FIRST_NAME,
                USER_A_LAST_NAME,
                USER_A_BIOGRAPHY,
                USER_A_IS_VERIFIED,
                encryptedPassword,
                USER_A_CREATED_AT,
                USER_A_ROLE
        );
    }

    public static UserEntity createSavedUserB() {
        String encryptedPassword = new BCryptPasswordEncoder().encode(USER_B_PASSWORD);
        return createSavedUserEntity(
                USER_B_ID,
                USER_B_USERNAME,
                USER_B_EMAIL,
                USER_B_FIRST_NAME,
                USER_B_LAST_NAME,
                USER_B_BIOGRAPHY,
                USER_B_IS_VERIFIED,
                encryptedPassword,
                USER_B_CREATED_AT,
                USER_B_ROLE
        );
    }

    public static UserEntity createSavedUserC() {
        String encryptedPassword = new BCryptPasswordEncoder().encode(USER_C_PASSWORD);
        return createSavedUserEntity(
                USER_C_ID,
                USER_C_USERNAME,
                USER_C_EMAIL,
                USER_C_FIRST_NAME,
                USER_C_LAST_NAME,
                USER_C_BIOGRAPHY,
                USER_C_IS_VERIFIED,
                encryptedPassword,
                USER_C_CREATED_AT,
                USER_C_ROLE
        );
    }

    public static UserEntity createSavedUserD() {
        String encryptedPassword = new BCryptPasswordEncoder().encode(USER_D_PASSWORD);
        return createSavedUserEntity(
                USER_D_ID,
                USER_D_USERNAME,
                USER_D_EMAIL,
                USER_D_FIRST_NAME,
                USER_D_LAST_NAME,
                USER_D_BIOGRAPHY,
                USER_D_IS_VERIFIED,
                encryptedPassword,
                USER_D_CREATED_AT,
                USER_D_ROLE
        );
    }

    public static UserProfileUpdateDTO createDefaultProfileUpdateDTO() {
        return createUserProfileUpdateDTO("UpdatedUsername", "UpdatedEmail", "UpdatedFirstName", "UpdatedLastName", "UpdatedBiography");
    }

    public static UserRegistrationDTO createUserRegistrationDTOA() {
        return createUserRegistrationDTO(
                USER_A_USERNAME,
                USER_A_EMAIL,
                USER_A_FIRST_NAME,
                USER_A_LAST_NAME,
                USER_A_PASSWORD
        );
    }

    public static UserDTO createUserDTOA() {
        return createUserDTO(
                USER_A_ID,
                USER_A_USERNAME,
                USER_A_EMAIL,
                USER_A_FIRST_NAME,
                USER_A_LAST_NAME,
                USER_A_BIOGRAPHY,
                USER_A_IS_VERIFIED,
                USER_A_CREATED_AT,
                USER_A_ROLE
        );
    }

    public static UserDTO createUserDTOB() {
        return createUserDTO(
                USER_B_ID,
                USER_B_USERNAME,
                USER_B_EMAIL,
                USER_B_FIRST_NAME,
                USER_B_LAST_NAME,
                USER_B_BIOGRAPHY,
                USER_B_IS_VERIFIED,
                USER_B_CREATED_AT,
                USER_B_ROLE
        );
    }

    public static UserDTO createUserDTOC() {
        return createUserDTO(
                USER_C_ID,
                USER_C_USERNAME,
                USER_C_EMAIL,
                USER_C_FIRST_NAME,
                USER_C_LAST_NAME,
                USER_C_BIOGRAPHY,
                USER_C_IS_VERIFIED,
                USER_C_CREATED_AT,
                USER_C_ROLE
        );
    }

    public static UserDTO createUserDTOD() {
        return createUserDTO(
                USER_D_ID,
                USER_D_USERNAME,
                USER_D_EMAIL,
                USER_D_FIRST_NAME,
                USER_D_LAST_NAME,
                USER_D_BIOGRAPHY,
                USER_D_IS_VERIFIED,
                USER_D_CREATED_AT,
                USER_D_ROLE
        );
    }

    // ***** TestUtil methods for the UserRelationshipEntity

    public static UserRelationshipEntity createSavedRelationship(Long id, UserEntity firstUser, UserEntity secondUser, UserRelationshipStatus status) {
        return UserRelationshipEntity.builder()
                .id(id)
                .firstUser(firstUser)
                .secondUser(secondUser)
                .status(status)
                .build();
    }

    /**
     * Creates a DTO version of the UserRelationshipEntity.
     *
     * NOTE: It could be a little better if we somehow use the actual userMapper
     */
    public static UserRelationshipDTO createRelationshipDTO(UserRelationshipEntity relationship) {

         UserEntity firstUser = relationship.getFirstUser();
         UserEntity secondUser = relationship.getSecondUser();
         UserDTO firstUserDTO = createUserDTO(
                 firstUser.getId(),
                 firstUser.getUsername(),
                 firstUser.getEmail(),
                 firstUser.getFirstName(),
                 firstUser.getLastName(),
                 firstUser.getBiography(),
                 firstUser.getIsVerified(),
                 firstUser.getCreatedAt(),
                 firstUser.getRole()
             );
         UserDTO secondUserDTO = createUserDTO(
                 secondUser.getId(),
                 secondUser.getUsername(),
                 secondUser.getEmail(),
                 secondUser.getFirstName(),
                 secondUser.getLastName(),
                 secondUser.getBiography(),
                 secondUser.getIsVerified(),
                 secondUser.getCreatedAt(),
                 secondUser.getRole()
         );
         return UserRelationshipDTO.builder()
                 .firstUser(firstUserDTO)
                 .secondUser(secondUserDTO)
                 .status(relationship.getStatus())
                 .build();
    }







    // ***** TestUtil methods for the ItemColorEntity and its related DTOs

    public static ItemColorCreateDTO createItemColorCreateDTO(String name, String hexCode) {
        return ItemColorCreateDTO.builder()
                .name(name)
                .hexCode(hexCode)
                .build();
    }
    public static ItemColorEntity createUnsavedItemColorEntity(String name, String hexCode) {
        return ItemColorEntity.builder()
                .name(name)
                .hexCode(hexCode)
                .build();
    }
    public static ItemColorEntity createSavedItemColorEntity(Long id, String name, String hexCode) {
        return ItemColorEntity.builder()
                .id(id)
                .name(name)
                .hexCode(hexCode)
                .build();
    }
    public static ItemColorDTO createItemColorDTO(Long id, String name, String hexCode) {
        return ItemColorDTO.builder()
                .id(id)
                .name(name)
                .hexCode(hexCode)
                .build();
    }

    // ***** Methods for creating default item colors
    public static ItemColorCreateDTO createItemColorCreateDTOA() {
        return createItemColorCreateDTO(ITEM_COLOR_A_NAME, ITEM_COLOR_A_HEXCODE);
    }
    public static ItemColorEntity createUnsavedItemColorA() {
        return createUnsavedItemColorEntity(ITEM_COLOR_A_NAME, ITEM_COLOR_A_HEXCODE);
    }
    public static ItemColorEntity createSavedItemColorA() {
        return createSavedItemColorEntity(ITEM_COLOR_A_ID, ITEM_COLOR_A_NAME, ITEM_COLOR_A_HEXCODE);
    }
    public static ItemColorDTO createItemColorDTOA() {
        return createItemColorDTO(ITEM_COLOR_A_ID, ITEM_COLOR_A_NAME, ITEM_COLOR_A_HEXCODE);
    }

    public static ItemColorCreateDTO createItemColorCreateDTOB() {
        return createItemColorCreateDTO(ITEM_COLOR_B_NAME, ITEM_COLOR_B_HEXCODE);
    }
    public static ItemColorEntity createUnsavedItemColorB() {
        return createUnsavedItemColorEntity(ITEM_COLOR_B_NAME, ITEM_COLOR_B_HEXCODE);
    }
    public static ItemColorEntity createSavedItemColorB() {
        return createSavedItemColorEntity(ITEM_COLOR_B_ID, ITEM_COLOR_B_NAME, ITEM_COLOR_B_HEXCODE);
    }
    public static ItemColorDTO createItemColorDTOB() {
        return createItemColorDTO(ITEM_COLOR_B_ID, ITEM_COLOR_B_NAME, ITEM_COLOR_B_HEXCODE);
    }





}

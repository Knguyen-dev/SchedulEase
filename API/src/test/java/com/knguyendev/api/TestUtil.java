package com.knguyendev.api;

import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.domain.entities.ItemColorEntity;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.enumeration.UserRole;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TestUtil {
    private TestUtil() {}

    /**
     * So we just need to create some test users. However the entities
     * need to conform to our UserEntity schema, so that we can at least
     * insert/save them into the database via the UserRepository without getting errors. Here are some
     * fields you need to include and things you need to do:
     *
     * 1. username
     * 2. firstName
     * 3. lastName
     * 4. email
     * 5. password: You'll enter a plaintext password, and then bcrypt it so that the hash becomes 60 characters!
     * 6. createdAt: This field can't be null. So here we'll put a ISO format 8601 date string
     * that's in zulu time. For pretend data, you can realistically any date-string utc here.
     * Though at least one example where we get the current time as a UTC date string would be great for reference.
     */
    public static UserEntity createTestUserA() {
        String plaintextPassword = "P$ssword_123";
        String encryptedPassword = new BCryptPasswordEncoder().encode(plaintextPassword);
        ZonedDateTime createdAt = ZonedDateTime.now(ZoneId.of("UTC"));

        return UserEntity.builder()
                .id(1L)
                .username("AbJacks0n")
                .email("AbJackson@gmail.com")
                .firstName("Abigail")
                .lastName("Jackson")
                .biography("Hi I'm a content creator on Youtube and sometimes I stream on Twitch")
                .isVerified(true)
                .password(encryptedPassword)
                .createdAt(createdAt)
                .role(UserRole.USER)
                .build();
    }


    /**
     * Creates another test user with sample data.
     */
    public static UserEntity createTestUserB() {
        String plaintextPassword = "AnotherSecurePass123";
        String encryptedPassword = new BCryptPasswordEncoder().encode(plaintextPassword);
        ZonedDateTime createdAt = ZonedDateTime.now(ZoneId.of("UTC"));

        return UserEntity.builder()
                .id(2L)
                .username("JaneDoe")
                .email("JaneDoe@example.com")
                .firstName("Jane")
                .lastName("Doe")
                .biography("Software engineer who loves to explore new technologies.")
                .isVerified(false)
                .password(encryptedPassword)
                .createdAt(createdAt)
                .role(UserRole.ADMIN)
                .build();
    }

    public static UserRegistrationDTO createBadUserRegistration() {
            return UserRegistrationDTO
                    .builder()
                    .build();
    }


    public static ItemColorEntity createTestItemColorA() {
        return ItemColorEntity.builder()
                .id(1L) // Assuming ID is auto-generated or set manually
                .name("Sky Blue")
                .hexCode("#87CEEB")
                .build();
    }

    public static ItemColorEntity createTestItemColorB() {
        return ItemColorEntity.builder()
                .id(2L) // Assuming ID is auto-generated or set manually
                .name("Coral")
                .hexCode("#FF7F50")
                .build();
    }



}

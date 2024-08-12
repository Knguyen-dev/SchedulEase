package com.knguyendev.api.domain.entities;

import com.knguyendev.api.enumeration.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;


/**
 * The UserEntity class implements Serializable to ensure that instances can be converted to a byte stream (Basically a
 * sequence of binary data). This is necessary for several reasons:
 * 1. **Session Storage**: When using Spring Session with Redis, session data—including the SecurityContext, which contains
 * UserDetails instances—needs to be serialized. Since UserDetails in turn contains UserEntity instances, UserEntity must
 * be serializable to be properly stored and retrieved from Redis.
 *
 * 2. **Session Management**: Serialization allows session data to be consistently transferred between the server and Redis.
 * This ensures that the SecurityContext and its associated UserEntity data are preserved across different requests and server instances.
 *
 * 3. **Local Server Storage**: Our session data is also going to be stored on the server as well, so our data needs to be
 * handled consistently.
 *
 * By implementing Serializable, we make sure that UserEntity can be serialized for storage in Redis or other mechanisms
 * that require object serialization.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="AppUser")
public class UserEntity implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="username", columnDefinition="VARCHAR(32) NOT NULL UNIQUE")
    private String username;

    @Column(name="firstName", columnDefinition="VARCHAR(64) NOT NULL")
    private String firstName;

    @Column(name="lastName", columnDefinition="VARCHAR(64) NOT NULL")
    private String lastName;

    @Column(name="biography", columnDefinition="VARCHAR(150) DEFAULT '' NOT NULL")
    private String biography = "";

    @Column(name="email", columnDefinition="VARCHAR(40) NOT NULL UNIQUE")
    private String email;

    @Column(name="isVerified", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVerified = false;

    @Column(name="emailToVerify", columnDefinition = "VARCHAR(40)")
    private String emailToVerify;

    @Column(name="verifyEmailToken", columnDefinition="CHAR(64)")
    private String verifyEmailToken;

    // NOTE: 'TIMESTAMPTZ', the 'Z' at the end lets the column store time-zone info
    @Column(name="verifyEmailTokenExpires", columnDefinition = "TIMESTAMP")
    private ZonedDateTime verifyEmailTokenExpires;

    @Column(name="password", columnDefinition = "CHAR(60) NOT NULL")
    private String password;

    @Column(name="passwordResetToken", columnDefinition = "CHAR(64)")
    private String passwordResetToken;

    @Column(name="passwordResetTokenExpires", columnDefinition = "TIMESTAMP")
    private ZonedDateTime passwordResetTokenExpires;

    @Column(name="createdAt", columnDefinition="TIMESTAMP NOT NULL")
    private ZonedDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable=false)
    private UserRole role;


    /**
     * Function for returning the authorities associated with a user
     * @return An immutable set that contains the single authority/role associated with the user.
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role.name()));
    }

    /**
     * Custom method to see if two UserEntities are equal.
     *
     * 1. If memory address references are equal, then it's the same user.
     * 2. If the object is null or of a different class, then it's not the same user.
     * 3. If the ID values are equal, then it's the same user since IDs are unique.
     *
     * @param o Object assumed to be a UserEntity
     * @return Boolean indicating whether two UserEntity objects are the same user
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id);
    }
}

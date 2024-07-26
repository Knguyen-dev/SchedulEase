package com.knguyendev.api.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

/**
 * Create a User entity:
 * Here we define the schema for our table. On startup, we should generate a table.
 *
 * We used 'columnDefinition' to more easily control the constraints and information about a column. We also defined
 * the name for each column when it gets generated to give us more control.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="username", columnDefinition="VARCHAR(32) NOT NULL UNIQUE")
    private String username;

    @Column(name="firstName", columnDefinition="VARCHAR(64) NOT NULL")
    private String firstName;

    @Column(name="lastName", columnDefinition="VARCHAR(64) NOT NULL")
    private String lastName;

    /*
    Remember that in java 'string' and 'String' are different. The latter
    which is capitalized 'String' can hold null as a valid value.
    */
    @Column(name="biography", columnDefinition="VARCHAR(150)")
    private String biography;

    @Column(name="email", columnDefinition="VARCHAR(40) NOT NULL UNIQUE")
    private String email;

    @Column(name="isVerified", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVerified = false;

    @Column(name="emailToVerify", columnDefinition = "VARCHAR(40)")
    private String emailToVerify;

    @Column(name="verifyEmailToken", columnDefinition="CHAR(64)")
    private String verifyEmailToken;

    // NOTE: 'TIMESTAMPTZ', the 'Z' at the end lets the column store time-zone info
    @Column(name="verifyEmailTokenExpires", columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime verifyEmailTokenExpires;

    @Column(name="password", columnDefinition = "CHAR(60) NOT NULL")
    private String password;

    @Column(name="passwordResetToken", columnDefinition = "CHAR(64)")
    private String passwordResetToken;

    @Column(name="passwordResetTokenExpires", columnDefinition = "TIMESTAMPTZ")
    private ZonedDateTime passwordResetTokenExpires;

    @Column(name="createdAt", columnDefinition="TIMESTAMPTZ NOT NULL")
    private ZonedDateTime createdAt;
}


/**
 * + What is LocalDateTime?
 * Represents a date-time without a timezone. Still in ISO-8601 format though.
 * It's good for storing timestamps when we don't need to track timezones explicitly.
 * So here we store timestamps relative to the server's timezone, which can be
 * configured.
 *
 * + ZonedDatetime:
 * Represents a date-time with a time zone (a ZoneId). As a result we can handle time zone conversions and maintain
 * time zone. Note that when inserting into a 'TIMESTAMP' column. Java's JDBC or JPA libraries handle the
 * conversion of 'ZonedDateTime' to a good 'TIMESTAMP' format with timezone info.
 *
 * We should be aware that if we need to convert timezones into UTC, we'd have to do it
 * programmatically. So here's the procedure:
 *
 * - Database:
 * The TIMESTAMP column will store date-time values including timezone information. So
 * if we insert a date-time value it'll be stored as is, including the timezone offset. So that's good it supports
 * ISO-8601 date strings.
 *
 * - Java App:
 * When we use 'LocalDateTime' it isn't going to store any timezone information. So it assumes
 * date-time is in the server's local time zone.
 *
 * - Handling UTC:
 * To ensure our app will store UTC date-time values in the 'TIMESTAMP' column.
 * Convert LocalDateTime to ZonedDateTime with UTC time zone (ZoneOffset.UTC) before inserting into the database.
 *
 */
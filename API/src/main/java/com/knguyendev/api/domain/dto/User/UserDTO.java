package com.knguyendev.api.domain.dto.User;
import com.knguyendev.api.enumeration.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * Create a UserDTO:
 * <p>
 * 1. With a DTO we decide what data is sent to the service layer from our presentation layer. So like
 *    we expect the request body's data to have a certain structure.
 * <p>
 * 2. It also decides the structure of data that we send out from our server. So we get data from the persistence
 *    layer, then map that to a dto, which may filter some data. For example, when sending user related
 *    info back to the client, we don't want to include sensitive fields such as password, the tokens, the
 *    token expiration times, and things since that's more sensitive information.
 * <p>
 * NOTE: Then we'll need some logic to effectively convert a UserDto to a UserEntity, and vice versa.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String biography;
    private Boolean isVerified;
    private ZonedDateTime createdAt;
    private UserRole role;
}

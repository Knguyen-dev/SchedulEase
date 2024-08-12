package com.knguyendev.api.repositories;


import com.knguyendev.api.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *
 */
@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    // We are expecting only one user here; it's crucial that you put the correct return types
    Optional<UserEntity> findByEmail(String email);

    // Finds a user by their username
    Optional<UserEntity> findByUsername(String username);

    /**
     * Finds a user by their email and password
     * @param email The email associated with a user's account.
     * @param password The hashed version of a user's password.
     * @return Potentially returns a UserEntity associated with the information.
     */
    Optional<UserEntity> findByEmailAndPassword(String email, String password);

    // Find a user by their username or email (good for checking when username and email are unique)
    Optional<UserEntity> findByUsernameOrEmail(String username, String email);

    /*
     * Finds all users where the ID is in our list of ids
     * @param ids An array of user ids that we want to check
     * @return
     */
    @Query("SELECT u FROM UserEntity u WHERE u.id IN :ids")
    List<UserEntity> findByIds(@Param("ids") List<Long> ids);
}

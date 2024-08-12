package com.knguyendev.api.repositories;


import com.knguyendev.api.domain.entities.UserRelationshipEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRelationshipRepository extends CrudRepository<UserRelationshipEntity, Long> {

    /**
     * Find a UserRelationship using the ID values of the first and second user
     * @param firstUserId ID of the first user
     * @param secondUserId ID of the second user
     * @return An Optional containing the UserRelationshipEntity if found, otherwise empty
     */
    @Query("SELECT ur FROM UserRelationshipEntity ur WHERE ur.firstUser.id = :firstUserId AND ur.secondUser.id = :secondUserId")
    Optional<UserRelationshipEntity> findByFirstUserIdAndSecondUserId(Long firstUserId, Long secondUserId);


    /**
     * Finds all relationships that involve the user. This is where 'userId' is either the firstUserId or secondUserId.
     * @param userId The id of the user that we're looking relationships for
     * @return A list of user relationships
     */
    @Query("SELECT ur FROM UserRelationshipEntity ur WHERE ur.firstUser.id = :userId OR ur.secondUser.id = :userId")
    List<UserRelationshipEntity> findByUserId(Long userId);

}

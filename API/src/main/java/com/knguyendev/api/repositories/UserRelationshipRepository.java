package com.knguyendev.api.repositories;


import com.knguyendev.api.domain.entities.UserRelationshipEntity;
import org.springframework.data.jpa.repository.Modifying;
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


    /**
     * Deletes all relationships involving the specified user.
     * @param userId The id of the user whose relationships should be deleted
     * <p>
     * NOTE: So it seems that when doing queries for deletions and modifications, you need to call this repository method
     * within a '@Transactional' annotation. Other-wise you get a 'TransactionRequiredException'. We use this method
     * in our service layer, and so the service function that is using this deletion query, will need to be annotated with
     * '@Transactional' such all database queries in that function happen within a transaction. As well, when you use
     * it in a unit test, you should use the same annotation on the test case.
     */
    @Modifying
    @Query("DELETE FROM UserRelationshipEntity ur WHERE ur.firstUser.id = :userId OR ur.secondUser.id = :userId")
    void deleteByUserId(Long userId);
}

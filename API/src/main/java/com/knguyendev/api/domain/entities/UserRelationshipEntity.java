package com.knguyendev.api.domain.entities;

import com.knguyendev.api.enumeration.UserRelationshipStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a relationship between two users in the system.
 * The relationship is defined by two users and a status that indicates the nature of the relationship.
 *
 * Design Considerations:
 *
 * 1. Auto-Generated Primary Key:
 *    - An auto-generated primary key (`id`) is used to uniquely identify each `UserRelationshipEntity` record.
 *    - Pros:
 *      - Simplifies record management by providing a unique identifier without manual intervention.
 *      - Ensures that each relationship is uniquely identifiable and can be easily referenced.
 *      - Generally more efficient for indexing and querying compared to composite keys.
 *    - Cons:
 *      - Adds a column to the table which is only used for identification purposes.
 *
 * Note: During the planning phase, the intention was to use a composite key based on `firstUserId` and `secondUserId`.
 *       However, due to challenges with managing composite keys, an auto-incrementing primary key was introduced.
 *       The current implementation does not use the primary key for most operations; it exists mainly for unique identification
 *       and potential future use. The decision on how to utilize this primary key in the future will be made later.
 * 2. Unique Constraint on User IDs:
 *    - The combination of `firstUserId` and `secondUserId` must be unique across the table.
 *    - This ensures that no duplicate relationships are created between the same pair of users.
 *    - Pros:
 *      - Prevents the creation of multiple records representing the same relationship between two users.
 *      - Maintains data integrity by enforcing uniqueness at the database level.
 *    - Cons:
 *      - Requires careful consideration of the uniqueness constraint to avoid conflicts in business logic.
 *
 * 3. Enforcing Order with `firstUserId < secondUserId`:
 *    - The constraint that `firstUserId` must be less than `secondUserId` helps to ensure a consistent and
 *      non-redundant representation of relationships.
 *    - This ordering guarantees that each pair of users is represented only once in the table:
 *      - If `firstUserId` and `secondUserId` are always ordered such that `firstUserId < secondUserId`,
 *      - It prevents the insertion of duplicate relationships like (user1, user2) and (user2, user1).
 *    - Pros:
 *      - Avoids redundancy and potential confusion by standardizing the order of user IDs.
 *      - Simplifies querying and comparison of relationships.
 *    - Cons:
 *      - Requires additional logic to enforce the ordering constraint before persisting data.
 *
 * Entity Definition:
 * - `id`: Auto-generated primary key for unique identification of each relationship.
 * - `firstUser`: Reference to the first user in the relationship.
 * - `secondUser`: Reference to the second user in the relationship.
 * - `status`: Status of the relationship.
 *
 * Validation:
 * - The `validateUserIds` method ensures that `firstUserId` is less than `secondUserId`.
 * - Throws an `IllegalArgumentException` if the constraint is violated to maintain data integrity.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "UserRelationship", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"firstUserId", "secondUserId"})
})
public class UserRelationshipEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(
            name="firstUserId",
            nullable=false,
            referencedColumnName = "id"
    )
    private UserEntity firstUser;

    @ManyToOne
    @JoinColumn(
            name="secondUserId",
            nullable=false,
            referencedColumnName = "id"
    )
    private UserEntity secondUser;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false)
    private UserRelationshipStatus status;

    @PrePersist
    @PreUpdate
    public void validateUserIds() {
        if (firstUser == null || secondUser == null) {
            return;
        }
        if (firstUser.getId() >= secondUser.getId()) {
            throw new IllegalArgumentException("UserRelationship constraint violation: firstUserId must be less than secondUserId!");
        }
    }
}

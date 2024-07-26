package com.knguyendev.api.repositories;


import com.knguyendev.api.domain.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Create UserRepository bean. It's basically a 'DAO', and ito will provide methods that will allow us to
 * interact with the table. Our UserService class will interact with an instance of this UserRepository to
 * do business logic such as finding a user, deleting a user, etc.
 * NOTE: It's CrudRepository<SomeEntity, Data type of SomeEntity's PK>
 *
 * + Methods Provided
 * 1. .save(SomeEntity)
 * 2. .saveAll(Iterable<SomeEntity> entities)
 * 3. .findById(ID)
 * 4. .existsById(ID)
 * 5. .findAll();
 * Among other pre-made functions that they give for you. Though the query functions
 * are mainly done via ID (Primary Key).
 *
 * + Custom methods:
 * But you can also create custom functions such as '.findByEmail(String email)'. Spring Data JPA
 * understands the method naming convention 'findBy...' and generates the appropriate query based on the
 * 'email' property. It also recognizes 'And' and 'Or' in your method names.
 *
 * 1. findBy + property name;
 * 2. deleteBy
 *
 * + What is JPQL and let's compare it against SQL
 *
 *
 * When we use JPQL, the results of the query are managed by the 'EntityManager'. As a result,
 * changes to the retrieved entities can be tracked and persisted by to the database.
 * For example, we want to fetch a user, update some info, and save those changes back to the database:
 *
 * @Transactional
 * public void updateUserEmail(String username, String newEmail) {
 *     Optional<UserEntity> optionalUser = userRepository.findByUsername("someUsername123");
 *     if (optionalUser.isPresent()) {
 *         UserEntity user = optionalUser.get();
 *         user.setEmail("newEmail");
 *
 *         // With the '@Transactional' annotation, the changes to the UserEntity are persisted in the
 *         // database when the function finishes since that's when the transaction commits.
 *         // However this only works when the UserEntity was gotten by JPQL, since the EntityManager
 *         // is the one that remembers the changes made to the UserEntity object.
 *
 *         Regardless of how you retrieved the entity (using JPQL or native SQL) you can always manually
 *         persist changes by using the .save() method of the repository.
 *     }
 * }
 *
 * With a Native SQL query, changes you make to
 * public UserEntity getUserDetails(String username) {
 *     Optional<UserEntity> optionalUser = userRepository.findByUsernameNative(username);
 *     return optionalUser.orElse(null);
 * }
 *
 * One last thing to consider is that if you use JPQL, if you change your database implementation
 * to another different relational database, then you don't have to change your query statements. So a given
 * JPQL query would work in MySQL, PostgreSQL, and other sql variants. Of course, there's a little performance overhead
 * when doing this, but it's not that bad.
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
}

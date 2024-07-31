package com.knguyendev.api.repositories;

import com.knguyendev.api.TestUtil;
import com.knguyendev.api.domain.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;
import java.util.List;
import java.util.stream.StreamSupport;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserRepositoryIntegrationTests: This will test our queries that we've created in the UserRepository.
 *
 */


@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserRepositoryIT {

    private final UserRepository underTest;

    /**
     * '@Autowired': In our regular 'production' code, Spring automatically injects dependencies into constructors. This is usually done implicitly by Spring in later versions. However, in test classes, you must explicitly use @Autowired to tell Spring to inject the required dependencies.
     *
     * - More in depth explanation: This annotation is used to automatically inject dependencies into constructors, setters,
     * or fields of a class. In the newer version of Spring, constructor-based dependency injection is recommended and often
     * '@Autowired' can be omitted as Spring automatically detects and injects dependencies. However, in tests classes, Spring's
     * automatic detection doesn't apply, so you need to explicitly use '@Autowired' to ensure that Spring knows to inject
     * the dependencies needed for the test. This is essential for setting up the test context correctly and for the tests
     * to run as expected.
     */
    @Autowired
    public UserRepositoryIT(UserRepository underTest) {
        this.underTest = underTest;
    }

    /*
    * NOTE: Comparing UserEntity objects based on their ID values rather than using
    * isEqualTo or equals methods is a deliberate choice to avoid complications
    * associated with date-time fields such as createdAt. Date-time fields can
    * be tricky due to potential truncation, rounding, or timezone differences
    * that occur when interacting with PostgresSQL. For instance, PostgresSQL might
    * round or truncate date-time values, or display time zones differently (e.g.,
    * using 'UTC' vs 'Z' for ZonedDateTime), leading to discrepancies even if
    * the original UserEntity objects are the same. To simplify the equality check
    * and ensure consistent comparison, we use the unique ID values which are
    * guaranteed to be consistent across different database queries and operations.
    *
    * */
    @Test
    public void testThatUserCanBeCreatedAndFound() {
        UserEntity userA = TestUtil.createTestUserA();
        underTest.save(userA);
        Optional<UserEntity> result = underTest.findById(userA.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(userA.getId());
    }

    @Test
    public void testThatManyUsersCanBeCreatedAndFound() {
        UserEntity userA = TestUtil.createTestUserA();
        underTest.save(userA);
        UserEntity userB = TestUtil.createTestUserB();
        underTest.save(userB);
        Iterable<UserEntity> result = underTest.findAll();
        List<Long> userIds = StreamSupport.stream(result.spliterator(), false)
                        .map(UserEntity::getId)
                                .toList();
        // Create a list of expected IDs
        List<Long> expectedIds = List.of(userA.getId(), userB.getId());

        // Assert that the actual IDs match the expected IDs
        assertThat(userIds)
                .hasSize(expectedIds.size()) // Check that the sizes are the same
                .containsExactlyInAnyOrderElementsOf(expectedIds); // Compare the lists of IDs
    }


    @Test
    public void testThatUserCanBeUpdated() {
        UserEntity userA = TestUtil.createTestUserA();

        // Insert it into database
        underTest.save(userA);

        // I'd with row already in the database, so this should update that row
        userA.setUsername("UpdatedUsername");
        underTest.save(userA);

        Optional<UserEntity> result = underTest.findById(userA.getId());
        assertThat(result).isPresent();

        // Assert that both entities have the same username.
        assertThat(result.get().getUsername()).isEqualTo(userA.getUsername());
    }

    @Test
    public void testThatUserCanBeDeleted() {
        UserEntity userA = TestUtil.createTestUserA();

        // Save and then delete UserEntity from the database
        underTest.save(userA);
        underTest.deleteById(userA.getId());

        // We expect things to be empty since we deleted it.
        Optional<UserEntity> result = underTest.findById(userA.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testFindByEmail() {
        UserEntity userA = TestUtil.createTestUserA();
        underTest.save(userA);
        Optional<UserEntity> result = underTest.findByEmail(userA.getEmail());
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(userA.getId());
    }

    @Test
    public void testFindByUsername() {
        UserEntity userA = TestUtil.createTestUserA();
        underTest.save(userA);
        Optional<UserEntity> result = underTest.findByUsername(userA.getUsername());
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo(userA.getUsername());
    }

    @Test
    public void testFindByEmailAndPassword() {
        UserEntity userA = TestUtil.createTestUserA();
        underTest.save(userA);
        Optional<UserEntity> result = underTest.findByEmailAndPassword(userA.getEmail(), userA.getPassword());
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(userA.getEmail());
        assertThat(result.get().getPassword()).isEqualTo(userA.getPassword());
    }

    @Test
    public void testFindByUsernameOrEmail() {
        UserEntity userA = TestUtil.createTestUserA();
        underTest.save(userA);

        // Check that user can be found via a good username
        Optional<UserEntity> result1 = underTest.findByUsernameOrEmail(userA.getUsername(), "BadEmail");
        assertThat(result1).isPresent();
        assertThat(result1.get().getId()).isEqualTo(userA.getId());

        // Check that they can be found via a good email
        Optional<UserEntity> result2 = underTest.findByUsernameOrEmail("BadUsername", userA.getEmail());
        assertThat(result2).isPresent();
        assertThat(result2.get().getId()).isEqualTo(userA.getId());
    }




}

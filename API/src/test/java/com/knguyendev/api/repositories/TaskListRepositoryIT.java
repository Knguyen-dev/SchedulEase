package com.knguyendev.api.repositories;

import com.knguyendev.api.TestUtil;
import com.knguyendev.api.domain.entities.TaskListEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskListRepositoryIT {

    private final TaskListRepository underTest;
    @Autowired
    public TaskListRepositoryIT(TaskListRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatTaskListCanBeCreatedAndFound() {
        TaskListEntity taskListA = TestUtil.createTaskListA();
        underTest.save(taskListA);
        Optional<TaskListEntity> result = underTest.findById(
                taskListA.getId()
        );
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(taskListA);
    }

    @Test
    public void testThatManyTaskListsCanBeCreatedAndFound() {
        TaskListEntity taskListA = TestUtil.createTaskListA();
        TaskListEntity taskListB = TestUtil.createTaskListB();
        underTest.save(taskListA);
        underTest.save(taskListB);
        Iterable<TaskListEntity> result = underTest.findAll();
        Assertions.assertThat(result)
                .hasSize(2).
                containsExactly(taskListA, taskListB);
    }

    @Test
    public void testThatTaskListCanBeDeleted() {
        TaskListEntity taskListA = TestUtil.createTaskListA();
        underTest.save(taskListA);
        underTest.deleteById(taskListA.getUserId());
        Optional<TaskListEntity> result = underTest.findById(taskListA.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testThatTaskListCanBeUpdated() {
        // Create and save TaskListA
        TaskListEntity taskListA = TestUtil.createTaskListA();
        underTest.save(taskListA);

        // Update the name of TaskListA
        taskListA.setName("New Name!");
        underTest.save(taskListA);

        // Fetch the updated TaskListA from the database
        Optional<TaskListEntity> result = underTest.findById(taskListA.getId());

        // Verify that the result is present and the name was updated
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("New Name!");
    }

    @Test
    public void testFindByUserId() {
        Long userId = 1L;
        TaskListEntity taskListA = TestUtil.createTaskListA();
        TaskListEntity taskListB = TestUtil.createTaskListB();
        taskListA.setUserId(userId);
        taskListB.setUserId(userId);
        underTest.save(taskListA);
        underTest.save(taskListB);
        List<TaskListEntity> taskLists = underTest.findByUserId(userId);
        assertThat(taskLists)
                .hasSize(2).
                containsExactly(taskListA, taskListB);
    }

    @Test
    @Transactional
    public void testDeleteByUserId() {
        Long userId = 1L;
        TaskListEntity taskListA = TestUtil.createTaskListA();
        TaskListEntity taskListB = TestUtil.createTaskListB();
        taskListA.setUserId(userId);
        taskListB.setUserId(userId);
        underTest.save(taskListA);
        underTest.save(taskListB);
        underTest.deleteByUserId(userId);
        List<TaskListEntity> taskLists = underTest.findByUserId(userId);
        assertThat(taskLists)
                .hasSize(0);
    }







}

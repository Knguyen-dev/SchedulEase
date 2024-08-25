package com.knguyendev.api.repositories;

import com.knguyendev.api.domain.entities.TaskEntity;
import com.knguyendev.api.domain.entities.TaskListEntity;
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
public class TaskRepositoryIT {
    private final TaskListRepository taskListRepository;
    private final TaskRepository underTest;

    @Autowired
    public TaskRepositoryIT(TaskListRepository taskListRepository, TaskRepository underTest) {
        this.taskListRepository = taskListRepository;
        this.underTest = underTest;
    }

    // test your stuff and even test the cascading
    @Test
    public void testThatTaskCanBeCreatedAndFound() {
        TaskEntity task = TaskEntity.builder()
                .id(1L)
                .taskListId(1L)
                .title("My Task")
                .description("A description task")
                .build();
        underTest.save(task);

        // Act
        Optional<TaskEntity> result = underTest.findById(task.getId());

        // Compare the ID values; remember that the taskList is lazy loaded, so you need to do getTaskId() and initialize it before we actually
        // try to compare it. Else we'd get a lazy loading error which makes sense as we were trying to compare things when the entire task wasn't loaded yet.
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(task.getId());
    }

    @Test
    public void testThatManyTasksCanBeCreatedAndFound() {
        TaskEntity taskA = TaskEntity.builder()
                .id(1L)
                .taskListId(1L)
                .title("A")
                .description("A")
                .build();
        TaskEntity taskB = TaskEntity.builder()
                .id(2L)
                .taskListId(1L)
                // Linking to prevTask isn't necessary, but we'll keep it for principle
                .prevTaskId(taskA.getId())
                .title("B")
                .description("B")
                .build();

        underTest.save(taskA);
        underTest.save(taskB);

        Iterable<TaskEntity> result = underTest.findAll();

        assertThat(result)
                .hasSize(2);
    }

    @Test
    public void testThatTaskCanBeDeleted() {
        TaskEntity taskA = TaskEntity.builder()
                .id(1L)
                .taskListId(1L)
                .title("A")
                .description("A")
                .build();
        underTest.save(taskA);
        underTest.deleteById(taskA.getId());
        Optional<TaskEntity> result = underTest.findById(taskA.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testThatTaskCanBeUpdated() {
        TaskEntity taskA = TaskEntity.builder()
                .id(1L)
                .taskListId(1L)
                .title("A")
                .description("A")
                .build();
        underTest.save(taskA);

        taskA.setTitle("Updated Title");
        underTest.save(taskA);

        Optional<TaskEntity> result = underTest.findById(taskA.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo(taskA.getTitle());
    }

    @Test
    public void testFindTaskAndSubtasksById() {
        TaskEntity taskA = TaskEntity.builder()
                .id(1L)
                .taskListId(1L)
                .title("A")
                .description("A")
                .build();
        TaskEntity taskB = TaskEntity.builder()
                .id(2L)
                .taskListId(1L)
                .parentTaskId(taskA.getId())
                // Linking to prevTask isn't necessary, but we'll keep it for principle
                .prevTaskId(taskA.getId())
                .title("B")
                .description("B")
                .build();
        TaskEntity taskC = TaskEntity.builder()
                .id(3L)
                .taskListId(1L)
                // Linking to prevTask isn't necessary, but we'll keep it for principle
                .prevTaskId(taskB.getId())
                .title("B")
                .description("B")
                .build();
        underTest.save(taskA);
        underTest.save(taskB);
        underTest.save(taskC);
        List<TaskEntity> tasks = underTest.findTaskAndSubtasksById(taskA.getId());
        assertThat(tasks).hasSize(2);
    }

    @Test
    public void testFindTaskListHeadByTaskListId() {
        Long taskListId = 1L;
        TaskEntity taskA = TaskEntity.builder()
                .id(1L)
                .taskListId(taskListId)
                // Ensure prevTask is null so that taskA is the head; nextTask doesn't need to be set
                .title("A")
                .description("A")
                .build();
        TaskEntity taskB = TaskEntity.builder()
                .id(2L)
                .taskListId(taskListId)
                // Ensure prevTask is defined, which simulates the idea that this isn't the head.
                .prevTaskId(taskA.getId())
                .title("B")
                .description("B")
                .build();
        underTest.save(taskA);
        underTest.save(taskB);
        Optional<TaskEntity> result = underTest.findTaskListHeadByTaskListId(taskListId);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(taskA);
    }

    @Test
    public void testFindTaskPrevNext() {
        Long taskListId = 1L;
        TaskEntity prevTask = TaskEntity.builder()
                .id(1L)
                .taskListId(taskListId)
                .title("A")
                .description("A")
                .build();
        TaskEntity task = TaskEntity.builder()
                .id(2L)
                .taskListId(taskListId)
                .title("B")
                .description("B")
                .build();
        TaskEntity nextTask = TaskEntity.builder()
                .id(3L)
                .taskListId(taskListId)
                .title("C")
                .description("C")
                .build();
        prevTask.setNextTaskId(task.getId());
        task.setPrevTaskId(prevTask.getId());
        task.setNextTaskId(nextTask.getId());
        nextTask.setPrevTaskId(task.getId());
        underTest.saveAll(List.of(prevTask, task, nextTask));
        List<TaskEntity> tasks = underTest.findTaskPrevNext(task.getId());
        assertThat(tasks).hasSize(3);
    }

    @Test
    public void testFindAllByIds() {
        Long taskListId = 1L;
        TaskEntity taskA = TaskEntity.builder()
                .id(1L)
                .taskListId(taskListId)
                .title("A")
                .build();
        TaskEntity taskB = TaskEntity.builder()
                .id(2L)
                .taskListId(taskListId)
                .title("B")
                .build();
        TaskEntity taskC = TaskEntity.builder()
                .id(3L)
                .taskListId(taskListId)
                .title("C")
                .build();
        underTest.saveAll(List.of(taskA, taskB, taskC));

        List<TaskEntity> tasks = underTest.findAllByIds(List.of(
                taskA.getId(),
                taskB.getId(),
                taskC.getId()
        ));
        assertThat(tasks).hasSize(3);

    }


    @Test
    @Transactional
    public void testDeleteTaskAndSubTasksById() {
        Long parentTaskId = 1L;
        Long taskListId = 1L;
        TaskEntity parentTask = TaskEntity.builder()
                .id(parentTaskId)
                .taskListId(taskListId)
                .title("Parent Task")
                .description("Parent Task Description")
                .build();
        TaskEntity childA = TaskEntity.builder()
                .id(parentTaskId + 1) // incrementing so that it doesn't conflict
                .taskListId(taskListId)
                .parentTaskId(parentTask.getId())
                .title("Child Task A")
                .description("Child Task A Description")
                .build();
        underTest.save(parentTask);
        underTest.save(childA);
        underTest.deleteTaskAndSubTasksById(parentTaskId);
        Iterable<TaskEntity> tasks = underTest.findAll();
        assertThat(tasks).hasSize(0);
    }

    @Test
    @Transactional
    public void testDeleteByTaskListId() {
        Long taskListId = 1L;
        TaskEntity taskA = TaskEntity.builder()
                .id(1L)
                .taskListId(taskListId)
                .title("A")
                .description("A")
                .build();
        TaskEntity taskB = TaskEntity.builder()
                .id(2L)
                .taskListId(taskListId)
                .title("B")
                .description("B")
                .build();
        underTest.save(taskA);
        underTest.save(taskB);
        underTest.deleteByTaskListId(taskListId);
        List<TaskEntity> tasks = underTest.findByTaskListId(taskListId);
        assertThat(tasks).hasSize(0);
    }

    @Test
    @Transactional
    public void testDeleteByUserId() {
        Long userId = 1L;

        TaskListEntity listA = TaskListEntity.builder()
                .id(1L)
                .name("List 1")
                .userId(userId)
                .isDefault(false)
                .build();
        TaskEntity taskA = TaskEntity.builder()
                .id(1L)
                .taskListId(listA.getId())
                .title("A")
                .description("A")
                .build();
        taskListRepository.save(listA);
        underTest.save(taskA);
        underTest.deleteByUserId(userId);

        Iterable<TaskEntity> tasks = underTest.findAll();
        assertThat(tasks).hasSize(0);
    }

    @Test
    public void findByUserId() {
        Long userId = 1L;

        TaskListEntity listA = TaskListEntity.builder()
                .id(1L)
                .name("List 1")
                .userId(userId)
                .isDefault(false)
                .build();
        TaskEntity taskA = TaskEntity.builder()
                .id(1L)
                .taskListId(listA.getId())
                .title("A")
                .description("A")
                .build();
        taskListRepository.save(listA);
        underTest.save(taskA);

        Iterable<TaskEntity> tasks = underTest.findByUserId(userId);
        assertThat(tasks).hasSize(1);
    }
}


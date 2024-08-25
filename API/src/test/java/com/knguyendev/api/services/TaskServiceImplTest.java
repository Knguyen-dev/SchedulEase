package com.knguyendev.api.services;

import com.knguyendev.api.domain.dto.Task.TaskCreateRequest;
import com.knguyendev.api.domain.dto.Task.TaskDTO;
import com.knguyendev.api.domain.dto.Task.TaskUpdateRequest;
import com.knguyendev.api.domain.entities.TaskEntity;
import com.knguyendev.api.domain.entities.TaskListEntity;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.mappers.TaskMapper;
import com.knguyendev.api.repositories.TaskListRepository;
import com.knguyendev.api.repositories.TaskRepository;
import com.knguyendev.api.services.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class) // allows us to mock in our tests
public class TaskServiceImplTest {
    @InjectMocks
    private TaskServiceImpl taskService;
    @Mock
    private TaskListRepository taskListRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;

    @Test
    void testGetLastSubTask() {
        // Arrange
        TaskEntity taskA = TaskEntity.builder()
                .id(1L)
                .taskListId(1L)
                .build();
        TaskEntity taskB = TaskEntity.builder()
                .id(2L)
                .taskListId(1L)
                .build();
        TaskEntity taskC = TaskEntity.builder()
                .id(3L)
                .taskListId(1L)
                .build();
        taskA.setNextTaskId(taskB.getId());
        taskB.setPrevTaskId(taskA.getId());
        taskB.setNextTaskId(taskC.getId());
        taskC.setPrevTaskId(taskB.getId());
        TaskEntity lastSubTask = taskService.getLastSubTask(
                List.of(taskA, taskB, taskC)
        );
        assertThat(taskC.getId()).isEqualTo(lastSubTask.getId());
    }

    @Test
    void testToggleTaskIndentationById_WhenNoOtherTasks() {
        // Arrange
        TaskEntity task = TaskEntity.builder()
                .id(1L)
                .taskListId(1L)
                .title("My Title")
                .description("My Description")
                .isComplete(false)
                .isStarred(false)
                .isAllDay(false)
                .dueDate(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
        // Simulate idea of only one task
        when(taskRepository.findTaskPrevNext(task.getId())).thenReturn(
                List.of(task)
        );
        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> taskService.toggleTaskIndentationById(task.getId()));
        // Assert and verify
        assertEquals("Task can't be indented or unindented since it is the only one in the list!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    void testToggleTaskIndentationById_WhenNotIndentedAndNoPrevTask() {
        // Arrange
        TaskEntity taskA = TaskEntity.builder()
                .id(1L)
                .taskListId(1L)
                .build();
        TaskEntity taskB = TaskEntity.builder()
                .id(2L)
                .taskListId(1L)
                .build();
        taskA.setNextTaskId(taskB.getId());
        taskB.setPrevTaskId(taskA.getId());
        // simulate
        when(taskRepository.findTaskPrevNext(taskA.getId())).thenReturn(
                List.of(taskA, taskB)
        );
        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> taskService.toggleTaskIndentationById(taskA.getId()));
        // Assert and verify
        assertEquals("Task that you want to indent is already at the top of its own task list!", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
    }

    @Test
    void testToggleTaskIndentationById_WhenNotIndentedAndPrevTaskParentTaskNotNull() {
        // Arrange
        TaskEntity prevTask = TaskEntity.builder()
                .id(1L)
                .taskListId(1L)
                .parentTaskId(3L)
                .build();
        TaskEntity task = TaskEntity.builder()
                .id(2L)
                .taskListId(1L)
                .build();
        prevTask.setNextTaskId(task.getId());
        task.setPrevTaskId(prevTask.getId());
        task.setNextTaskId(null);
        // Assume it's not a subtask
        task.setParentTaskId(null);
        // Simulate getting the target task and its parent (prevTask)
        when(taskRepository.findTaskPrevNext(task.getId())).thenReturn(
                List.of(prevTask, task)
        );
        // Act
        taskService.toggleTaskIndentationById(task.getId());
        // Assert that the parent id was assigned correctly
        assertThat(task.getParentTaskId()).isEqualTo(prevTask.getParentTaskId());
        verify(taskRepository).save(task);
    }

    @Test
    void testToggleTaskIndentationById_WhenNotIndentedAndPrevTaskParentNull() {
        // Arrange
        TaskEntity prevTask = TaskEntity.builder()
                .id(1L)
                .taskListId(1L)
                .build();
        TaskEntity task = TaskEntity.builder()
                .id(2L)
                .taskListId(1L)
                .build();
        prevTask.setNextTaskId(task.getId());
        task.setPrevTaskId(prevTask.getId());

        // Simulate getting the target task and its parent (prevTask)
        when(taskRepository.findTaskPrevNext(task.getId())).thenReturn(
                List.of(prevTask, task)
        );
        // Act
        taskService.toggleTaskIndentationById(task.getId());
        // Assert that the parent id was assigned correctly
        assertThat(task.getParentTaskId()).isEqualTo(prevTask.getId());
        verify(taskRepository).save(task);
    }

    @Test
    void testToggleTaskIndentationById_WhenIndentedAndNextTaskNull() {
        TaskEntity parentTask = TaskEntity.builder()
                .id(1L)
                .taskListId(1L)
                .build();
        TaskEntity task = TaskEntity.builder()
                .id(2L)
                .taskListId(1L)
                .build();
        parentTask.setNextTaskId(task.getId());
        task.setPrevTaskId(parentTask.getId());
        task.setParentTaskId(parentTask.getId());
        // Simulate
        when(taskRepository.findTaskPrevNext(task.getId())).thenReturn(
                List.of(parentTask, task)
        );
        when(taskRepository.findTaskAndSubtasksById(task.getParentTaskId())).thenReturn(
                List.of(parentTask, task)
        );
        // Act
        taskService.toggleTaskIndentationById(task.getId());
        // Assert
        assertThat(task.getParentTaskId()).isNull();
        verify(taskRepository).save(task);
    }

    @Test
    void testToggleTaskIndentationById_WhenIndentedAndNextTaskIsSubtask() {
        // Arrange
        TaskEntity parentTask = TaskEntity.builder()
                .id(1L)
                .taskListId(1L)
                .build();
        TaskEntity task = TaskEntity.builder()
                .id(2L)
                .taskListId(1L)
                .build();
        TaskEntity nextTask = TaskEntity.builder()
                .id(3L)
                .taskListId(1L)
                .build();
        // parent, target, and next (acts as last subtask)
        parentTask.setNextTaskId(task.getId());
        task.setPrevTaskId(parentTask.getId());
        task.setNextTaskId(nextTask.getId());
        task.setParentTaskId(parentTask.getId());
        nextTask.setPrevTaskId(task.getId());
        nextTask.setNextTaskId(null);
        nextTask.setParentTaskId(parentTask.getId());

        // Simulate
        when(taskRepository.findTaskPrevNext(task.getId())).thenReturn(
                List.of(parentTask, task, nextTask)
        );
        when(taskRepository.findTaskAndSubtasksById(task.getParentTaskId())).thenReturn(
                List.of(parentTask, task, nextTask)
        );
        // Act
        taskService.toggleTaskIndentationById(task.getId());
        // Assert
        verify(taskRepository).findTaskPrevNext(task.getId());
        verify(taskRepository, never()).findById(anyLong());
        // Create an ArgumentCaptor for the Iterable<TaskEntity>
        ArgumentCaptor<Iterable<TaskEntity>> captor = ArgumentCaptor.forClass(Iterable.class);
        // Verify the saveAll method was called and capture the argument
        verify(taskRepository).saveAll(captor.capture());
        // Get the captured value and convert it to a List for easier assertions
        List<TaskEntity> capturedTasks = new ArrayList<>();
        captor.getValue().forEach(capturedTasks::add);
        // Define your expected list of TaskEntity objects
        List<TaskEntity> expectedTasks = List.of(
                parentTask,
                task,
                nextTask
        );
        // Assert the captured list contains exactly the expected tasks
        assertThat(capturedTasks).containsExactlyInAnyOrderElementsOf(expectedTasks);
    }

    @Test
    void testUpdateById_WhenUnchanged() {
        // Arrange
        TaskEntity task = TaskEntity.builder()
                .id(1L)
                .taskListId(1L)
                .title("My Title")
                .description("My Description")
                .isComplete(false)
                .isStarred(false)
                .isAllDay(false)
                .dueDate(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();

        TaskUpdateRequest taskUpdateRequest = TaskUpdateRequest.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .isComplete(task.isComplete())
                .isAllDay(task.isAllDay())
                .dueDate(task.getDueDate())
                .build();

        // Simulate getting the task, things should stay the same.
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskMapper.toDTO(task)).thenReturn(new TaskDTO());

        // Act
        taskService.updateById(task.getId(), taskUpdateRequest);

        // Assert, the save function shouldn't have been called
        verify(taskRepository, never()).save(task);
    }

    @Test
    void testUpdateById_WhenChanged() {
        // Arrange
        TaskEntity task = TaskEntity.builder()
                .id(1L)
                .taskListId(1L)
                .title("My Title")
                .description("My Description")
                .isComplete(false)
                .isStarred(false)
                .isAllDay(false)
                .dueDate(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();

        TaskUpdateRequest taskUpdateRequest = TaskUpdateRequest.builder()
                .title("Updated Title") // The title has changed, so we expect the function to update things
                .description(task.getDescription())
                .isComplete(task.isComplete())
                .isAllDay(task.isAllDay())
                .dueDate(task.getDueDate())
                .build();

        // Simulate
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskMapper.toDTO(task)).thenReturn(new TaskDTO());

        // Act
        taskService.updateById(task.getId(), taskUpdateRequest);

        // Assert, the save function should have been called
        verify(taskRepository).save(task);
    }

    @Test
    void testDeleteById_WhenNonParentTask() {
        // Arrange
        TaskEntity prevTask = TaskEntity.builder()
                .id(1L)
                .build();
        TaskEntity task = TaskEntity.builder()
                .id(2L)
                .build();
        TaskEntity nextTask = TaskEntity.builder()
                        .id(3L)
                        .build();
        prevTask.setNextTaskId(task.getId());
        task.setPrevTaskId(prevTask.getId());
        task.setNextTaskId(nextTask.getId());
        nextTask.setPrevTaskId(task.getId());
        // Simulate finding the task and prev task, then simulate finding no subtasks
        when(taskRepository.findTaskAndSubtasksById(task.getId())).thenReturn(
                List.of(prevTask, task, nextTask)
        );
        when(taskRepository.findAllByIds(List.of(task.getPrevTaskId(), task.getNextTaskId()))).thenReturn(
                List.of(prevTask, nextTask)
        );
        // Act
        taskService.deleteById(task.getId());
        // Assert and verify
        verify(taskRepository).saveAll(List.of(prevTask, nextTask));
        verify(taskRepository).deleteById(task.getId());
    }

    @Test
    void testDeleteById_WhenParentTask() {
        // Arrange
        // Task before the parent task.
        TaskEntity prevParentTask = TaskEntity.builder()
                .id(1L)
                .build();
        TaskEntity parentTask = TaskEntity.builder()
                .id(2L)
                .build();
        // The only and last subtask
        TaskEntity taskA = TaskEntity.builder()
                .id(3L)
                .build();
        // Task following the last subtask
        TaskEntity taskB = TaskEntity.builder()
                .id(4L)
                .build();
        prevParentTask.setNextTaskId(parentTask.getId());
        parentTask.setPrevTaskId(prevParentTask.getId());
        parentTask.setNextTaskId(taskA.getId());
        taskA.setPrevTaskId(parentTask.getId());
        taskA.setNextTaskId(taskB.getId());
        taskA.setParentTaskId(parentTask.getId());
        taskB.setPrevTaskId(taskA.getId());
        // Simulate
        when(taskRepository.findTaskAndSubtasksById(parentTask.getId())).thenReturn(
                List.of(parentTask, taskA)
        );
        when(taskRepository.findAllByIds(List.of(prevParentTask.getId(), taskB.getId()))).thenReturn(
                List.of(prevParentTask, taskB)
        );
        // Act
        taskService.deleteById(parentTask.getId());
        // Assert and verify
        verify(taskRepository).saveAll(List.of(prevParentTask, taskB));
        verify(taskRepository).deleteTaskAndSubTasksById(parentTask.getId());
    }

    @Test
    void testCreateTaskAtTopOfTaskList_WhenOtherHead() {
        // Arrange
        TaskListEntity taskList = TaskListEntity.builder()
                .id(1L)
                .build();
        TaskEntity otherHead = TaskEntity.builder()
                .taskListId(1L)
                .build();
        TaskEntity newTask = TaskEntity.builder()
                .taskListId(1L)
                .title("Title")
                .description("Description")
                .isComplete(true)
                .isAllDay(false)
                .isStarred(true)
                .dueDate(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
        TaskCreateRequest taskCreateRequest = TaskCreateRequest.builder()
                .taskListId(newTask.getTaskListId())
                .title(newTask.getTitle())
                .description(newTask.getDescription())
                .isComplete(newTask.isComplete())
                .isAllDay(newTask.isAllDay())
                .isStarred(newTask.isStarred())
                .dueDate(newTask.getDueDate())
                .build();
        // Simulate
        when(taskListRepository.findById(taskCreateRequest.getTaskListId())).thenReturn(Optional.of(taskList));
        when(taskRepository.findTaskListHeadByTaskListId(newTask.getTaskListId())).thenReturn(
                Optional.of(otherHead)
        );
        when(taskRepository.save(newTask)).thenReturn(newTask);
        // Act
        TaskDTO resultDTO = taskService.create(taskCreateRequest);
        // Assert and verify
        verify(taskRepository).save(newTask);
        verify(taskRepository).save(otherHead);
    }

    @Test
    void testCreateTaskAtTopOfTaskList_WhenNoOtherHead() {
        // Arrange
        TaskListEntity taskList = TaskListEntity.builder()
                .id(1L)
                .build();
        TaskEntity newTask = TaskEntity.builder()
                .taskListId(taskList.getId())
                .title("Title")
                .description("Description")
                .isComplete(true)
                .isAllDay(false)
                .isStarred(true)
                .dueDate(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
        TaskCreateRequest taskCreateRequest = TaskCreateRequest.builder()
                .taskListId(newTask.getTaskListId())
                .title(newTask.getTitle())
                .description(newTask.getDescription())
                .isComplete(newTask.isComplete())
                .isAllDay(newTask.isAllDay())
                .isStarred(newTask.isStarred())
                .dueDate(newTask.getDueDate())
                .build();
        // Simulate
        when(taskListRepository.findById(taskCreateRequest.getTaskListId())).thenReturn(Optional.of(taskList));
        when(taskRepository.findTaskListHeadByTaskListId(newTask.getTaskListId())).thenReturn(
                Optional.empty() // simulating the idea of the the task list having no tasks within it.
        );
        when(taskRepository.save(newTask)).thenReturn(newTask);
        // Act
        TaskDTO resultDTO = taskService.create(taskCreateRequest);

        // Assert and verify
        verify(taskRepository).save(newTask);
    }

//    @Test
//    void testCreateTask_WhenPrevTaskIsParentTask() {
//        // Arrange
//        TaskEntity parentTask = TaskEntity.builder() // the prevTask indicated in request
//                .id(1L)
//                .taskListId(1L)
//                .build();
//        TaskEntity taskA = TaskEntity.builder() // subtask of the prevTask
//                .id(2L)
//                .taskListId(1L)
//                .build();
//        parentTask.setNextTaskId(taskA.getId());
//        taskA.setPrevTaskId(parentTask.getId());
//        taskA.setParentTaskId(parentTask.getId());
//        TaskEntity newTask = TaskEntity.builder()
//                .taskListId(1L)
//                .prevTaskId(parentTask.getId())
//                .nextTaskId(taskA.getId())
//                .title("Title")
//                .description("Description")
//                .isComplete(true)
//                .isAllDay(false)
//                .isStarred(true)
//                .dueDate(ZonedDateTime.now(ZoneId.of("UTC")))
//                .build();
//        TaskCreateRequest taskCreateRequest = TaskCreateRequest.builder()
//                .prevTaskId(parentTask.getId())
//                .title(newTask.getTitle())
//                .description(newTask.getDescription())
//                .isComplete(newTask.isComplete())
//                .isAllDay(newTask.isAllDay())
//                .isStarred(newTask.isStarred())
//                .dueDate(newTask.getDueDate())
//                .build();
//        // Simulate
//        when(taskRepository.findTaskAndSubtasksById(taskCreateRequest.getPrevTaskId()))
//                .thenReturn(
//                        List.of(parentTask, taskA)
//                );
//        when(taskRepository.save(newTask)).thenReturn(newTask);
//        // Act
//        taskService.create(taskCreateRequest);
//        // Assert and verify
//        verify(taskRepository).saveAll(List.of(parentTask, taskA));
//    }
//    @Test
//    void testCreateTask_WhenPrevTaskIsSubTask() {}
//    @Test
//    void testCreateTask_WhenPrevTaskIsBaseTask() {}
}

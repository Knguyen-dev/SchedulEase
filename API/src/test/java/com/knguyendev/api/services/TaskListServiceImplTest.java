package com.knguyendev.api.services;

import com.knguyendev.api.TestUtil;
import com.knguyendev.api.domain.dto.TaskList.TaskListDTO;
import com.knguyendev.api.domain.dto.TaskList.TaskListRequest;
import com.knguyendev.api.domain.entities.TaskListEntity;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.mappers.TaskListMapper;
import com.knguyendev.api.repositories.TaskListRepository;
import com.knguyendev.api.services.impl.TaskListServiceImpl;
import com.knguyendev.api.utils.AuthUtils;
import com.knguyendev.api.utils.ServiceUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // allows us to mock in our tests
public class TaskListServiceImplTest {
    @InjectMocks
    private TaskListServiceImpl taskListService;
    @Mock
    private TaskListRepository taskListRepository;
    @Mock
    private TaskListMapper taskListMapper;
    @Mock
    private AuthUtils authUtils;
    @Mock
    private ServiceUtils serviceUtils;

    @Test
    void testCreateTaskList_Success() {
        // Arrange
        Long authUserId = 1L;
        TaskListRequest request = new TaskListRequest("New Task List");

        TaskListEntity oldTaskList = TestUtil.createTaskList(null, authUserId, "New Task List", false);
        TaskListEntity newTaskList = TestUtil.createTaskList(1L, authUserId, "New Task List", false);
        TaskListDTO taskListDTO = TestUtil.createTaskListDTO(1L, authUserId, "New Task List", false);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUserId);
        when(serviceUtils.getUserById(authUserId)).thenReturn(new UserEntity()); // Mock as needed
        when(taskListRepository.save(oldTaskList)).thenReturn(newTaskList);
        when(taskListMapper.toDTO(newTaskList)).thenReturn(taskListDTO);

        // Act
        TaskListDTO result = taskListService.create(request);

        // Assert and verify
        assertEquals(taskListDTO, result);
        verify(authUtils).getAuthUserId();
        verify(serviceUtils).getUserById(authUserId);
        verify(taskListRepository).save(oldTaskList);
        verify(taskListMapper).toDTO(newTaskList);
    }

    @Test
    void testUpdateTaskList_Success() {
        // Arrange
        Long taskListId = 1L;
        Long userId = 1L;
        Long authUserId = 1L;
        TaskListRequest request = new TaskListRequest("Updated Task List");
        TaskListEntity existingTaskList = TestUtil.createTaskList(taskListId, userId, "Old Name", false);
        TaskListEntity updatedTaskList = TestUtil.createTaskList(taskListId, userId, request.getName(), false);
        TaskListDTO taskListDTO = TestUtil.createTaskListDTO(taskListId, userId, "Updated Name", false);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUserId);
        when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(existingTaskList));
        when(taskListRepository.save(updatedTaskList)).thenReturn(updatedTaskList);
        when(taskListMapper.toDTO(updatedTaskList)).thenReturn(taskListDTO);

        // Act
        TaskListDTO result = taskListService.update(taskListId, request);

        // Assert and Verify
        assertEquals(taskListDTO, result);
        verify(taskListRepository).findById(taskListId);
        verify(taskListRepository).save(updatedTaskList); // Ensure the save method is called with correct argument
        verify(taskListMapper).toDTO(updatedTaskList);
    }

    @Test
    void testUpdateDefaultTaskList_Failure() {
        // Arrange
        Long id = 1L;
        Long authUserId = 1L;
        TaskListRequest request = new TaskListRequest("Updated Task List");
        TaskListEntity defaultTaskList = TestUtil.createTaskList(id, authUserId, "Updated task list", true);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUserId);
        when(taskListRepository.findById(id)).thenReturn(Optional.of(defaultTaskList));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> taskListService.update(id, request));

        // Assert and verify
        assertEquals("You cannot update the default task list!", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
        verify(taskListRepository).findById(id);
    }

    @Test
    void testUpdateWhenTaskListNotFound() {
        // Arrange
        Long id = 1L;
        TaskListRequest request = new TaskListRequest("Updated Task List");

        // Simulate
        when(taskListRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> taskListService.update(id, request));

        // Assert and verify
        assertEquals("The task list with id '" + id + "' wasn't found!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(taskListRepository).findById(id);
    }

    @Test
    void testUpdateTaskList_NotOwned_Failure() {
        // Arrange
        Long id = 1L;
        Long authUserId = 1L;
        Long otherUserId = 2L;
        TaskListRequest request = new TaskListRequest("Updated Task List");
        TaskListEntity defaultTaskList = TestUtil.createTaskList(id, otherUserId, "Updated task list", true);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUserId);
        when(taskListRepository.findById(id)).thenReturn(Optional.of(defaultTaskList));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> taskListService.update(id, request));

        // Assert and verify
        assertEquals("You cannot update this task list since you don't own it!", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
        verify(taskListRepository).findById(id);
    }

    @Test
    void testDeleteTaskList_Success() {
        Long id = 1L;
        Long authUserId = 1L;
        TaskListEntity taskList = TestUtil.createTaskList(id, authUserId, "TaskList", false);

        when(authUtils.getAuthUserId()).thenReturn(authUserId);
        when(taskListRepository.findById(id)).thenReturn(Optional.of(taskList));

        taskListService.delete(id);

        verify(taskListRepository).findById(id);
        verify(taskListRepository).deleteById(id);
    }

    @Test
    void testDeleteTaskList_NotOwned_Failure() {
        // Arrange
        Long id = 1L;
        Long authUserId = 1L;
        Long otherUserId = 2L;

        TaskListEntity taskList = TestUtil.createTaskList(id, otherUserId, "TaskList", false);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUserId);
        when(taskListRepository.findById(id)).thenReturn(Optional.of(taskList));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> taskListService.delete(id));

        // Assert and verify
        assertEquals("You don't own this task list, so you can't delete it!", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
        verify(taskListRepository).findById(id);
    }

    @Test
    void testDeleteDefaultTaskList_Failure() {
        // Arrange
        Long id = 1L;
        Long authUserId = 1L;
        TaskListEntity taskList = TestUtil.createTaskList(id, authUserId, "TaskList", true);

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUserId);
        when(taskListRepository.findById(id)).thenReturn(Optional.of(taskList));

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> taskListService.delete(id));

        // Assert and verify
        assertEquals("You cannot delete a default task list!", exception.getMessage());
        assertEquals(HttpStatus.FORBIDDEN, exception.getHttpStatus());
        verify(taskListRepository).findById(id);
    }

    @Test
    void testDeleteTaskListWhenNotFound_Failure() {
        // Arrange
        Long id = 1L;

        // Simulate
        when(taskListRepository.findById(id)).thenReturn(Optional.empty());

        // Act
        ServiceException exception = assertThrows(ServiceException.class, () -> taskListService.delete(id));

        // Assert and verify
        assertEquals("The task list with id '" + id + "' wasn't found!", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        verify(taskListRepository).findById(id);
    }

    @Test
    void testFindUserTaskLists_Success() {
        // Arrange
        Long authUserId = 1L;
        List<TaskListEntity> taskLists = Arrays.asList(
                TestUtil.createTaskList(1L, authUserId, "Task List 1", true),
                TestUtil.createTaskList(2L, authUserId, "Task List 2", false)
        );
        List<TaskListDTO> taskListDTOs = Arrays.asList(
                TestUtil.createTaskListDTO(1L, authUserId, "Task List 1", true),
                TestUtil.createTaskListDTO(2L, authUserId, "Task List 2", false)
        );

        // Simulate
        when(authUtils.getAuthUserId()).thenReturn(authUserId);
        when(taskListRepository.findByUserId(authUserId)).thenReturn(taskLists);
        when(taskListMapper.toDTO(any(TaskListEntity.class))).thenReturn(taskListDTOs.get(0), taskListDTOs.get(1));

        // Act
        List<TaskListDTO> result = taskListService.findUserTaskLists(false);

        // Assert and verify
        assertEquals(taskListDTOs, result);
        verify(authUtils).getAuthUserId();
        verify(taskListRepository).findByUserId(authUserId);
    }
}

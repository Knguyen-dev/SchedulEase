package com.knguyendev.api.services.impl;

import com.knguyendev.api.domain.dto.TaskList.TaskListDTO;
import com.knguyendev.api.domain.dto.TaskList.TaskListRequest;
import com.knguyendev.api.domain.entities.TaskListEntity;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.mappers.TaskListMapper;
import com.knguyendev.api.repositories.TaskListRepository;
import com.knguyendev.api.services.TaskListService;
import com.knguyendev.api.utils.AuthUtils;
import com.knguyendev.api.utils.ServiceUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class TaskListServiceImpl implements TaskListService {
    private final TaskListRepository taskListRepository;
    private final TaskListMapper taskListMapper;
    private final AuthUtils authUtils;
    private final ServiceUtils serviceUtils;
    public TaskListServiceImpl(TaskListRepository taskListRepository, TaskListMapper taskListMapper, AuthUtils authUtils, ServiceUtils serviceUtils) {
        this.taskListRepository = taskListRepository;
        this.taskListMapper = taskListMapper;
        this.authUtils = authUtils;
        this.serviceUtils = serviceUtils;
    }


    /**
     * Function that validates whether the authenticated user can modify (update or delete) an existing task list.
     *
     * @param taskList The TaskListEntity that's being updated.
     * @param isUpdate Boolean indicating whether it's an update operation or not. This is crucial for determining the error message used.
     */
    private TaskListEntity validateModifyTaskList(Long taskListId, boolean isUpdate) throws ServiceException {
        // Attempt to find the taskList and throw error if it's not found
        Optional<TaskListEntity> result = taskListRepository.findById(taskListId);
        if (result.isEmpty()) {
            throw new ServiceException("The task list with id '" + taskListId + "' wasn't found!", HttpStatus.NOT_FOUND);
        }
        TaskListEntity taskList = result.get();

        // Define error messages for our error cases.
        String ownershipErrorMessage = isUpdate ? "You cannot update this task list since you don't own it!" : "You don't own this task list, so you can't delete it!";
        String isDefaultErrorMessage = isUpdate ? "You cannot update the default task list!" : "You cannot delete a default task list!";

        // If the auth. user isn't the owner of the taskList, then throw the error
        Long authUserId = authUtils.getAuthUserId();
        if (!taskList.getUserId().equals(authUserId)) {
            throw new ServiceException(ownershipErrorMessage, HttpStatus.FORBIDDEN);
        }

        // If the auth. user is trying to modify a default taskList, then throw an error
        if (taskList.isDefault()) {
            throw new ServiceException(isDefaultErrorMessage, HttpStatus.FORBIDDEN);
        }

        // Everything should be good, so return the TaskListEntity for further processing
        return taskList;
    }

    @Override
    public TaskListDTO create(TaskListRequest taskListRequest) {
        // Get the ID if teh authenticated user; I think we can just assume they're logged in, but we need a failsafe for data-integrity
        Long authUserId = authUtils.getAuthUserId();

        // Ensure that the authenticated user still exists in the database
        serviceUtils.getUserById(authUserId);

        // Create a new task list with isDefault = false
        TaskListEntity newTaskList = TaskListEntity.builder()
                .userId(authUserId)
                .name(taskListRequest.getName())
                .isDefault(false)
                .build();

        return taskListMapper.toDTO(taskListRepository.save(newTaskList));
    }

    @Override
    public TaskListDTO update(Long id, TaskListRequest taskListRequest) {

        // Check if the authenticated user owns this taskList
        TaskListEntity taskList = validateModifyTaskList(id, true);

        // If the name didn't change, then return the dto early
        if (taskList.getName().equals(taskListRequest.getName())) {
            return taskListMapper.toDTO(taskList);
        }

        // At this point we know the name has changed, so we'll need to apply changes
        // And then save it to the database.
        taskList.setName(taskListRequest.getName());
        return taskListMapper.toDTO(taskListRepository.save(taskList));
    }

    @Override
    public void delete(Long id) {
        TaskListEntity taskList = validateModifyTaskList(id, false);
        taskListRepository.deleteById(taskList.getId());
    }

    @Override
    public List<TaskListDTO> findUserTaskLists(boolean includeTasks) {
        Long authUserId = authUtils.getAuthUserId();
        // NOTE: We haven't included the tasks associated yet but don't worry
        List<TaskListEntity> taskLists = taskListRepository.findByUserId(authUserId);
        return taskLists.stream().map(taskListMapper::toDTO).toList();
    }


    public List<TaskListDTO> findAll() {
        Iterable<TaskListEntity> taskLists = taskListRepository.findAll();
        return StreamSupport.stream(taskLists.spliterator(), false)
                .map(taskListMapper::toDTO)
                .collect(Collectors.toList());
    }

}

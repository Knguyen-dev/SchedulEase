package com.knguyendev.api.services.impl;

import com.knguyendev.api.domain.dto.Task.TaskDTO;
import com.knguyendev.api.domain.dto.TaskList.TaskListDTO;
import com.knguyendev.api.domain.dto.TaskList.TaskListRequest;
import com.knguyendev.api.domain.entities.TaskEntity;
import com.knguyendev.api.domain.entities.TaskListEntity;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.mappers.TaskListMapper;
import com.knguyendev.api.mappers.TaskMapper;
import com.knguyendev.api.repositories.TaskListRepository;
import com.knguyendev.api.repositories.TaskRepository;
import com.knguyendev.api.services.TaskListService;
import com.knguyendev.api.utils.AuthUtils;
import com.knguyendev.api.utils.ServiceUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class TaskListServiceImpl implements TaskListService {
    private final TaskListRepository taskListRepository;
    private final TaskRepository taskRepository;
    private final TaskListMapper taskListMapper;
    private final TaskMapper taskMapper;

    private final AuthUtils authUtils;
    private final ServiceUtils serviceUtils;
    public TaskListServiceImpl(TaskListRepository taskListRepository, TaskRepository taskRepository, TaskListMapper taskListMapper, TaskMapper taskMapper, AuthUtils authUtils, ServiceUtils serviceUtils) {
        this.taskListRepository = taskListRepository;
        this.taskRepository = taskRepository;
        this.taskListMapper = taskListMapper;
        this.taskMapper = taskMapper;
        this.authUtils = authUtils;
        this.serviceUtils = serviceUtils;
    }


    /**
     * Function that validates whether the authenticated user can modify (update or delete) an existing task list.
     *
     * @param taskListId The TaskListEntity that's being updated.
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
        String isDefaultErrorMessage = "You cannot delete a default task list!";

        // If the auth. user isn't the owner of the taskList, then throw the error
        Long authUserId = authUtils.getAuthUserId();
        if (!taskList.getUserId().equals(authUserId)) {
            throw new ServiceException(ownershipErrorMessage, HttpStatus.FORBIDDEN);
        }

        /*
         * If the user is trying to delete a default task, then throw an error.
         *
         * NOTE: We'll let the user update a default task, which will allowing them to change things such as the name.
         */
        if (!isUpdate && taskList.isDefault()) {
            throw new ServiceException(isDefaultErrorMessage, HttpStatus.FORBIDDEN);
        }

        // Everything should be good, so return the TaskListEntity for further processing
        return taskList;
    }


    private List<TaskDTO> sortTasks(List<TaskDTO> tasks) {
        // Dictionary with key being ID and value being the associated task
        Map<Long, TaskDTO> taskMap = new HashMap<>();
        tasks.forEach(t -> taskMap.put(t.getId(), t));
        // Find the head task (the one with no previous task)
        TaskDTO headTask = tasks.stream()
                .filter(t -> t.getPrevTaskId() == null)
                .findFirst()
                .orElseThrow(() -> new ServiceException("Task in task list didn't have a head!", HttpStatus.INTERNAL_SERVER_ERROR));

        // List to hold the sorted tasks
        List<TaskDTO> sortedTasks = new ArrayList<>();
        TaskDTO currentTask = headTask;
        while (currentTask != null) {
            sortedTasks.add(currentTask);
            Long nextTaskId = currentTask.getNextTaskId();
            // If nextTaskId exists, fetch the full representation from the taskMap
            if (nextTaskId != null) {
                currentTask = taskMap.get(nextTaskId);
            } else {
                // No next task, so assign currentTask to null
                currentTask = null;
            }
        }
        return sortedTasks;
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
    @Transactional
    public void delete(Long id) {
        TaskListEntity taskList = validateModifyTaskList(id, false);
        // Delete the tasks associated with the task list, and then the task lists themselves.
        taskRepository.deleteByTaskListId(taskList.getId());
        taskListRepository.deleteById(taskList.getId());
    }

    @Override
    public List<TaskListDTO> findUserTaskLists(boolean includeTasks) {
        Long authUserId = authUtils.getAuthUserId();
        List<TaskListDTO> taskLists =
                taskListRepository.findByUserId(authUserId)
                        .stream()
                        .map(taskListMapper::toDTO)
                        .toList();
        if (includeTasks) {
            // Get all tasks associated with the user
            List<TaskEntity> userTasks = taskRepository.findByUserId(authUserId);
            // Dictionary to hold task lists with their associated tasks
            Map<Long, List<TaskDTO>> tasksByTaskListId = new HashMap<>();
            // Create dictionary with format {taskListId : List<TaskDTO>}
            for (TaskEntity task : userTasks) {
                Long taskListId = task.getTaskListId();
                TaskDTO taskDTO = taskMapper.toDTO(task);
                tasksByTaskListId
                        .computeIfAbsent(taskListId, k -> new ArrayList<>())
                        .add(taskDTO);
            }
            // Assign tasks to their corresponding task lists, also sort the tasks as well.
            for (TaskListDTO taskList : taskLists) {
                List<TaskDTO> tasks = tasksByTaskListId.get(taskList.getId());
                if (tasks != null) { // Null check is important to cover the case where a taskList doesn't have any tasks
                    taskList.setTasks(sortTasks(tasks));
                }
            }
        }

        return taskLists;
    }

    public List<TaskListDTO> findAll() {
        Iterable<TaskListEntity> taskLists = taskListRepository.findAll();
        return StreamSupport.stream(taskLists.spliterator(), false)
                .map(taskListMapper::toDTO)
                .collect(Collectors.toList());
    }

}

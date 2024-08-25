package com.knguyendev.api.services;

import com.knguyendev.api.domain.dto.Task.TaskDTO;
import com.knguyendev.api.domain.dto.Task.TaskCreateRequest;
import com.knguyendev.api.domain.dto.Task.TaskUpdateRequest;
import com.knguyendev.api.exception.ServiceException;

public interface TaskService {

    /**
     * Service used to create a task
     * @param taskCreateRequest Information needed to create a new task in the database
     * @return A DTO representing the newly created task
     * @throws ServiceException An exception that's thrown when we run into an error creating the task in the service layer
     */
    TaskDTO create(TaskCreateRequest taskCreateRequest) throws ServiceException;

    /**
     * Service for updating the contents of an existing task. Note: If you want to update indentation, ordering,
     * or any referential key related things, then use the other functions.
     * @param id ID of the existing task that's being updated.
     * @param taskUpdateRequest DTO that contains information for updating the task
     * @return DTO representing the newly updated task
     * @throws ServiceException An exception thrown in the service layer when we have problems updating a task.
     */
    TaskDTO updateById(Long id, TaskUpdateRequest taskUpdateRequest) throws ServiceException;

    /**
     * Toggles whether a task was being indented
     * @param id ID of the task
     * @return The DTO of the task that's your indenting and unindenting
     * @throws ServiceException Exception thrown when there's an error
     */
    TaskDTO toggleTaskIndentationById(Long id) throws ServiceException;

    /**
     * Deletes a task via it's ID
     * @param id ID of the task being deleted
     * @throws ServiceException Exception thrown when we have an issue deleting the task in the service layer.
     */
    void deleteById(Long id) throws ServiceException;

    /**
     * Finds a task via its ID
     * @param id ID of the task being fetched
     * @return DTO of the fetched task
     * @throws ServiceException An exception if there was an error finding the task
     */
    TaskDTO findById(Long id) throws ServiceException;
}

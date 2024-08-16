package com.knguyendev.api.services;

import com.knguyendev.api.domain.dto.TaskList.TaskListDTO;
import com.knguyendev.api.domain.dto.TaskList.TaskListRequest;

import java.util.List;

public interface TaskListService {

    /**
     * Creates a new non-default taskList for the currently authenticated user
     * @param taskListRequest Request DTO containing all user inputted information for creating a taskList
     * @return A DTO representing the task list that was created
     */
    TaskListDTO create(TaskListRequest taskListRequest);

    /**
     * Attempts to update an existing taskList
     * @param id ID of the taskList that we're updating
     * @param taskListRequest Request DTO containing new info that we're saving to the taskList
     * @return A DTO representing the workout that was updated
     */
    TaskListDTO update(Long id, TaskListRequest taskListRequest);

    /**
     * Deletes a taskList associated with the authenticated user. This should only delete
     * non-default taskList, and any attempt to delete the default taskList should be rejected.
     * @param id ID of the taskList being deleted.
     */
    void delete(Long id);

    /**
     * Finds all task lists associated with the authenticated user
     * @param includeTasks Boolean that indicates whether to include the associated tasks within the taskList
     * @return A DTO representing the taskList
     */
    List<TaskListDTO> findUserTaskLists(boolean includeTasks);


    /**
     * Finds all task lists in the database. Mainly used for debugging.
     *
     */
    List<TaskListDTO> findAll();


    //    void createDefaultTaskListForUser(Long userId);


    //  void deleteTaskListsByUserId(Long userId);



    /*
     * + Other issues we have to address and the idea of clean coding:
     *
     * 1. In our userService.registerUser(), we want to create a taskList for that user. So how do we do this?
     *    A separate service function? One more generalized to accept the userId and also accept a parameter for isDefault?
     *    It's this, or you'd instantiate TaskListEntity inside the userService method, and save it.
     *
     * 2. Also, when a user is deleted all of their taskLists are deleted, which also means the taskList with isDefault
     * is also deleted. I actually have a repository method for this with
     *
     * 'taskListRepository.deleteByUserId()' which deletes all taskLists associated with a user, regardless of isDefault
     * being true or not. So I think the plan would be 'TaskListService.deleteById()' would be for deleting non-default
     * taskLists, whilst the repository method is for deleting all taskLists
     *
     * + Credits:
     * https://stackoverflow.com/questions/17485800/mvc-can-a-service-depend-on-other-service
     *
     */
}

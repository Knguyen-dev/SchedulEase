package com.knguyendev.api.controllers;


import com.knguyendev.api.domain.dto.TaskList.TaskListDTO;
import com.knguyendev.api.domain.dto.TaskList.TaskListRequest;
import com.knguyendev.api.services.TaskListService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/taskLists")
public class TaskListController {
    private final TaskListService taskListService;
    public TaskListController(TaskListService taskListService) {
        this.taskListService = taskListService;
    }

    /**
     * Create a new task list for the currently authenticated user.
     * The task list will be created based on the details provided in the TaskListRequest DTO.
     *
     * @param taskListRequest The request body containing task list details.
     * @return ResponseEntity with the created task list information.
     */
    @PostMapping(path="")
    public ResponseEntity<TaskListDTO> createTaskList(@Valid @RequestBody TaskListRequest taskListRequest) {
        taskListRequest.normalizeData();
        TaskListDTO taskListDTO = taskListService.create(taskListRequest);
        return new ResponseEntity<>(taskListDTO, HttpStatus.OK);
    }

    /**
     * Update an existing task list for the currently authenticated user.
     * The task list to be updated is identified by the provided ID.
     *
     * @param id The ID of the task list to be updated.
     * @param taskListRequest The request body containing updated task list details.
     * @return ResponseEntity with the updated task list information.
     */
    @PutMapping(path="/{id}")
    public ResponseEntity<TaskListDTO> updateTaskList(@PathVariable Long id, @Valid @RequestBody TaskListRequest taskListRequest) {
        taskListRequest.normalizeData();
        TaskListDTO taskListDTO = taskListService.update(id, taskListRequest);
        return new ResponseEntity<>(taskListDTO, HttpStatus.OK);
    }

    /**
     * Delete an existing task list for the currently authenticated user.
     * The task list to be deleted is identified by the provided ID.
     *
     * @param id The ID of the task list to be deleted.
     * @return ResponseEntity with status indicating success or failure of the operation.
     */
    @DeleteMapping(path="/{id}")
    public ResponseEntity<Void> deleteTaskList(@PathVariable Long id) {
        taskListService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Retrieve all task lists associated with the currently authenticated user.
     * Optionally include related tasks based on query parameter.
     *
     * @param includeTasks Flag indicating whether to include related tasks in the response.
     * @return ResponseEntity with a list of task lists and optionally their tasks.
     */
    @GetMapping(path="")
    public ResponseEntity<List<TaskListDTO>> getAllTaskLists(@RequestParam(value = "includeTasks", defaultValue = "false") boolean includeTasks) {
        List<TaskListDTO> dtoList = taskListService.findUserTaskLists(includeTasks);
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }


    /**
     * Retrieve all task lists in the system.
     * This is typically used for debugging or administrative purposes.
     *
     * @return ResponseEntity with a list of all task lists.
     */
    @GetMapping(path="/all")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<List<TaskListDTO>> getAllTaskListsForDebugging() {
        List<TaskListDTO> dtoList = taskListService.findAll();
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }
}
/*
 * Some apis
 *
 * + Create a new taskList; this wouldn't be a default taskList. I guess this would create
 * a taskList for the currently authenticated user, so we'll rely on the backend to get that from session data
 * You'd pass in the TaskListRequest 'DTO' for creating it
 * POST "/taskLists"
 *
 * + Update an existing taskList; So this would update an existing taskList. Since taskList has a
 * primary key you could just pass that in as a route parameter. You'd still pass in the 'TaskListRequest'
 * DTO. The only thing you have to worry about is making sure that if there does exist a taskList with that 'id',
 * does it belong to the currently authenticated user? If yes, then they're modifying their own taskList, else no and reject
 * the request since other users can't mess with your own taskList.
 * PUT "/taskList/${id}"
 *
 * + Delete an existing taskList; By standards it's probably going to be a two query thing such that we make sure that
 * the taskList exists before deleting it. That aligns with the patterns we already do. Then of course the idea would be
 * to make sure that the authenticated user owns the taskList, which is our way of making sure if they're allowed to delete it.
 * DELETE "/taskList/${id}
 *
 *
 * + Get all task lists associated with a user. More specifically the authenticated user
 * In this, our service will probably include the related tasks associated with them, but we'll
 * handle that later. I don't really see us getting the taskLists without the individual tasks themselves
 * so yeah. I think it's fine to do GET "/taskLists" and only return the taskLists associated with the authenticated
 * user., as this is a protected route as well. I think the main issue is deciding whether to include the tasks
 * with the taskList, I guess for flexibility, for now I think we should have a query parameter that decides whether     *  to include the tasks.
 */
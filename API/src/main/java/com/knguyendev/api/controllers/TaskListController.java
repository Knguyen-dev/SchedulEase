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

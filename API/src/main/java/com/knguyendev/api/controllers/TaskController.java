package com.knguyendev.api.controllers;
import com.knguyendev.api.domain.dto.Task.TaskCreateRequest;
import com.knguyendev.api.domain.dto.Task.TaskDTO;
import com.knguyendev.api.domain.dto.Task.TaskUpdateRequest;
import com.knguyendev.api.repositories.TaskRepository;
import com.knguyendev.api.services.ItemColorService;
import com.knguyendev.api.services.TaskService;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /*
     * Creating brand new task.
     * POST "/"
     */
    @PostMapping(path="")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskCreateRequest taskCreateRequest) {
        TaskDTO taskDTO = taskService.create(taskCreateRequest);
        return new ResponseEntity<>(taskDTO, HttpStatus.OK);
    }

    /*
     * Updating a task, more specifically its contents. Here you should also be able to mark it as complete or incomplete
     * whether it's marked as starred, or is all day
     * PUT /{taskId}
     */
    @PatchMapping(path="/{id}/toggle-indent")
    public ResponseEntity<TaskDTO> toggleTaskIndentation(@PathVariable("id") Long id) {
        TaskDTO taskDTO = taskService.toggleTaskIndentationById(id);
        return new ResponseEntity<>(taskDTO, HttpStatus.OK);
    }

    /*
     * Updating a task's contents.
     * PATCH /{taskId}
     */
    @PatchMapping(path="/{id}")
    public ResponseEntity<TaskDTO> updateTaskById(@PathVariable("id") Long id, @Valid @RequestBody TaskUpdateRequest taskUpdateRequest) {
        taskUpdateRequest.normalizeData();
        TaskDTO taskDTO = taskService.updateById(id, taskUpdateRequest);
        return new ResponseEntity<>(taskDTO, HttpStatus.OK);
    }

    /*
     * Deleting a single task
     * DELETE /{taskId}
     */
    @DeleteMapping(path="/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable("id") Long id) {
        taskService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /*
     * Reading an existing task
     * GET /{taskId}
     */
    @GetMapping(path="/{id}")
    public ResponseEntity<TaskDTO> findTaskById(@PathVariable("id") Long id) {
        TaskDTO taskDTO = taskService.findById(id);
        return new ResponseEntity<>(taskDTO, HttpStatus.OK);
    }
}

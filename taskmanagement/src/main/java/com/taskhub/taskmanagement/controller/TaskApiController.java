package com.taskhub.taskmanagement.controller;


import com.taskhub.taskmanagement.entity.Task;
import com.taskhub.taskmanagement.exception.TaskValidationException;
import com.taskhub.taskmanagement.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskApiController {
    @Autowired
    private TaskService taskService;
    @Operation(summary = "get all tasks or get task based on search ")
    @GetMapping
    public ResponseEntity<List<Task>> getTasks(@Parameter(description = "Search query") @RequestParam(required = false) String query) {
            List<Task> tasks;
            if (query != null && !query.isEmpty()) {
                tasks = taskService.searchTasks(query);
            } else {
                tasks = taskService.getAllTasks();
            }
            return ResponseEntity.ok(tasks);

    }
    @Operation(summary = "get task by its id")
    @GetMapping("/{taskId}")
    public ResponseEntity<Task> getTaskById(@Parameter(description = "Id of the task to retrieve") @PathVariable Long taskId) {
        Task task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

@Operation(summary = "create a task")
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody Task task, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
        throw new TaskValidationException(bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (acc, curr) -> acc + curr + ". "));

    }
            Task createdTask = taskService.createTask(task);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);

    }

    @Operation(summary = "update a task")
    @PutMapping("/{taskId}")
    public ResponseEntity<Task> updateTask(@Parameter(description = "Id of the task to update") @PathVariable Long taskId,@Valid @RequestBody Task task,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new TaskValidationException(bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .reduce("", (acc, curr) -> acc + curr + ". "));

        }
        task.setTaskId(taskId);
        Task updatedTask = taskService.updateTask(task);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "delete a task")
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@Parameter(description = "Id of the task to delete") @PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "get all todo tasks")
    @GetMapping("/status/todo")
    public ResponseEntity<List<Task>> getTodoTasks() {
        List<Task> tasks = taskService.getTasksByStatus(com.taskhub.taskmanagement.entity.TaskStatus.TODO);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "get all tasks which are in progress")
    @GetMapping("/status/in-progress")
    public ResponseEntity<List<Task>> getInProgressTasks() {
        List<Task> tasks = taskService.getTasksByStatus(com.taskhub.taskmanagement.entity.TaskStatus.IN_PROGRESS);
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "get list of all completed tasks")
    @GetMapping("/status/done")
    public ResponseEntity<List<Task>> getDoneTasks() {
        List<Task> tasks = taskService.getTasksByStatus(com.taskhub.taskmanagement.entity.TaskStatus.DONE);
        return ResponseEntity.ok(tasks);
    }

}

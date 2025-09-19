package com.taskhub.taskmanagement.controller;

import com.taskhub.taskmanagement.entity.Task;
import com.taskhub.taskmanagement.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import com.taskhub.taskmanagement.controller.TaskApiController;
import com.taskhub.taskmanagement.entity.Task;
import com.taskhub.taskmanagement.entity.TaskStatus;
import com.taskhub.taskmanagement.exception.TaskValidationException;
import com.taskhub.taskmanagement.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TaskApiControllerTest {
    @Mock
    private TaskService taskService;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private TaskApiController taskApiController;

    @Test
    void testGetTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task());
        when(taskService.getAllTasks()).thenReturn(tasks);
        ResponseEntity<List<Task>> response = taskApiController.getTasks(null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
    }
    @Test
    void testGetTaskById() {
        Task task = new Task();
        when(taskService.getTaskById(anyLong())).thenReturn(task);
        ResponseEntity<Task> response = taskApiController.getTaskById(1L);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task, response.getBody());
    }
    @Test
    void testCreateTask() {
        Task task = new Task();
        when(taskService.createTask(any(Task.class))).thenReturn(task);
        ResponseEntity<Task> response = taskApiController.createTask(task, bindingResult);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(task, response.getBody());
    }
    @Test
    void testCreateTaskValidationException() {
        when(bindingResult.hasErrors()).thenReturn(true);
        assertThrows(TaskValidationException.class, () -> taskApiController.createTask(new Task(), bindingResult));
    }
    @Test
    void testUpdateTask() {
        Task task = new Task();
        when(taskService.updateTask(any(Task.class))).thenReturn(task);
        ResponseEntity<Task> response = taskApiController.updateTask(1L, task, bindingResult);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(task, response.getBody());
    }
    @Test
    void testUpdateTaskValidationException() {
        when(bindingResult.hasErrors()).thenReturn(true);
        assertThrows(TaskValidationException.class, () -> taskApiController.updateTask(1L, new Task(), bindingResult));
    }
    @Test
    void testDeleteTask() {
        ResponseEntity<Void> response = taskApiController.deleteTask(1L);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService, times(1)).deleteTask(1L);
    }
    @Test
    void testGetTodoTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task());
        when(taskService.getTasksByStatus(TaskStatus.TODO)).thenReturn(tasks);
        ResponseEntity<List<Task>> response = taskApiController.getTodoTasks();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
    }
    @Test
    void testGetInProgressTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task());
        when(taskService.getTasksByStatus(TaskStatus.IN_PROGRESS)).thenReturn(tasks);
        ResponseEntity<List<Task>> response = taskApiController.getInProgressTasks();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
    }
    @Test
    void testGetDoneTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task());
        when(taskService.getTasksByStatus(TaskStatus.DONE)).thenReturn(tasks);
        ResponseEntity<List<Task>> response = taskApiController.getDoneTasks();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
    }
    @Test
    void testGetTasksWithQuery() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task());
        when(taskService.searchTasks(anyString())).thenReturn(tasks);
        ResponseEntity<List<Task>> response = taskApiController.getTasks("query");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tasks, response.getBody());
    }

    }

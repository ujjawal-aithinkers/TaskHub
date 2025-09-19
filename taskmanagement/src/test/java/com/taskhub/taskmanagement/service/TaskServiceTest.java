package com.taskhub.taskmanagement.service;

import com.taskhub.taskmanagement.entity.Task;
import com.taskhub.taskmanagement.entity.TaskStatus;
import com.taskhub.taskmanagement.exception.TaskNotFoundException;
import com.taskhub.taskmanagement.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.times;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.times;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    @Test
    public void testGetAllTasks() {
        List<Task> tasks = new ArrayList<>();
        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAllTasks();

        assertEquals(tasks, result);
    }

    @Test
    public void testGetTaskById() {
        Task task = new Task();
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(1L);

        assertEquals(task, result);
    }

    @Test
    public void testCreateTask() {
        Task task = new Task();
        when(taskRepository.save(task)).thenReturn(task);
        when(taskRepository.findByTaskNameAndTaskDescriptionAndProjectIdAndCategory(any(), any(), any(), any())).thenReturn(new ArrayList<>());

        Task result = taskService.createTask(task);

        assertEquals(task, result);
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task();
        task.setTaskId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenReturn(task);

        Task result = taskService.updateTask(task);
         assertEquals(task, result);
    }

    @Test
    public void testDeleteTask() {
        taskService.deleteTask(1L);
        verify(taskRepository, Mockito.times(1)).deleteById(1L);
    }
    @Test
    public void testGetTaskByIdNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1L));
    }
    @Test
    public void testCreateTaskAlreadyExists() {
        Task task = new Task();
        when(taskRepository.findByTaskNameAndTaskDescriptionAndProjectIdAndCategory(any(), any(), any(), any())).thenReturn(List.of(task));
        assertThrows(RuntimeException.class, () -> taskService.createTask(task));
    }

    @Test
    public void testUpdateTaskNotFound() {
        Task task = new Task();
        task.setTaskId(1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(task));
    }
    @Test
    public void testSearchTasks() {
        List<Task> tasks = new ArrayList<>();
        when(taskRepository.searchTasks(any())).thenReturn(tasks);
        List<Task> result = taskService.searchTasks("query");
        assertEquals(tasks, result);
    }

    @Test
    public void testGetTasksByStatusTodo() {
        Task task = new Task();
        task.setStatus(TaskStatus.TODO);
        when(taskRepository.findByStatus(TaskStatus.TODO)).thenReturn(List.of(task));
        List<Task> tasks = taskService.getTasksByStatus(TaskStatus.TODO);
        assertEquals(1, tasks.size());
        assertEquals(TaskStatus.TODO, tasks.get(0).getStatus());
    }

    @Test
    public void testGetTasksByStatusInProgress() {
        Task task = new Task();
        task.setStatus(TaskStatus.IN_PROGRESS);
        when(taskRepository.findByStatus(TaskStatus.IN_PROGRESS)).thenReturn(List.of(task));
        List<Task> tasks = taskService.getTasksByStatus(TaskStatus.IN_PROGRESS);
        assertEquals(1, tasks.size());
        assertEquals(TaskStatus.IN_PROGRESS, tasks.get(0).getStatus());
    }

    @Test
    public void testGetTasksByStatusDone() {
        Task task = new Task();
        task.setStatus(TaskStatus.DONE);
        when(taskRepository.findByStatus(TaskStatus.DONE)).thenReturn(List.of(task));
        List<Task> tasks = taskService.getTasksByStatus(TaskStatus.DONE);
        assertEquals(1, tasks.size());
        assertEquals(TaskStatus.DONE, tasks.get(0).getStatus());
    }


}



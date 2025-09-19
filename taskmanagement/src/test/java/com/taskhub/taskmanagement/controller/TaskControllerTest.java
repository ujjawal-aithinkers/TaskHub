package com.taskhub.taskmanagement.controller;

import com.taskhub.taskmanagement.repository.TaskRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.taskhub.taskmanagement.entity.Task;
import com.taskhub.taskmanagement.entity.TaskCategory;
import com.taskhub.taskmanagement.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {
    @Mock
    private TaskService taskService;
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private Model model;

    @InjectMocks
    private TaskController taskController;
    @Test
    public void testGetAllTasks() {
        List<Task> tasks = new ArrayList<>();
        when(taskService.getAllTasks()).thenReturn(tasks);
        String viewName = taskController.getTasks(null, model);
        assertEquals("tasks", viewName);
        verify(model, times(1)).addAttribute("tasks", tasks);
    }

    @Test
    public void testGetTaskById() {
        Task task = new Task();
        when(taskService.getTaskById(1L)).thenReturn(task);
        String viewName = taskController.getTaskById(1L, model);
        assertEquals("task", viewName);
        verify(model, times(1)).addAttribute("task", task);
    }

    @Test
    public void testCreateTaskForm() {
        String viewName = taskController.createTaskForm(model);
        assertEquals("create-task", viewName);
        verify(model, times(1)).addAttribute(eq("task"), any(Task.class));
    }

    @Test
    public void testCreateTask() {
        Task task = new Task();
        when(taskService.createTask(task)).thenReturn(task);
        String viewName = taskController.createTask(task, new BeanPropertyBindingResult(task, "task"), model);
        assertEquals("redirect:/tasks", viewName);
    }

    @Test
    public void testCreateTaskWithError() {
        Task task = new Task();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(task, "task");
        bindingResult.addError(new ObjectError("task", "Error message"));
        String viewName = taskController.createTask(task, bindingResult, model);
        assertEquals("create-task", viewName);
        verify(model, times(1)).addAttribute("task", task);
    }
    @Test
    public void testUpdateTaskForm() {
        Task task = new Task();
        when(taskService.getTaskById(1L)).thenReturn(task);
        String viewName = taskController.updateTaskForm(1L, model);
        assertEquals("update-task", viewName);
        verify(model, times(1)).addAttribute("task", task);
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task();
        BindingResult bindingResult = new BeanPropertyBindingResult(task, "task");
        bindingResult.addError(new ObjectError("task", "Error message"));
        String viewName = taskController.updateTask(1L, task, model,bindingResult);
        assertEquals("update-task", viewName);
    }

    @Test
    public void testDeleteTask() {
        String viewName = taskController.deleteTask(1L);
        assertEquals("redirect:/tasks", viewName);
        verify(taskService, times(1)).deleteTask(1L);
}
    @Test
    public void testGetTasksWithQuery() {
        List<Task> tasks = new ArrayList<>();
        when(taskService.searchTasks("query")).thenReturn(tasks);
        String viewName = taskController.getTasks("query", model);
        assertEquals("tasks", viewName);
        verify(model, times(1)).addAttribute("tasks", tasks);
    }
    }

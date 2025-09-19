package com.taskhub.taskmanagement.repository;

import com.taskhub.taskmanagement.entity.Task;
import com.taskhub.taskmanagement.entity.TaskCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskRepositoryTest {
    @Mock
    private TaskRepository taskRepository;

    @Test
    public void testFindByTaskNameAndTaskDescriptionAndAssignedToId() {
        Task task = new Task();
        task.setTaskName("Test Task");
        task.setTaskDescription("Test Description");
        task.setAssignedToId(1L);
        when(taskRepository.findByTaskNameAndTaskDescriptionAndAssignedToId(
                "Test Task", "Test Description", 1L))
                .thenReturn(List.of(task));

        List<Task> result = taskRepository.findByTaskNameAndTaskDescriptionAndAssignedToId(
                "Test Task", "Test Description", 1L);

        assertEquals(1, result.size());
        assertEquals(task, result.get(0));
        verify(taskRepository, times(1)).findByTaskNameAndTaskDescriptionAndAssignedToId(
                "Test Task", "Test Description", 1L);

    }

    @Test
    public void testFindByTaskNameAndTaskDescriptionAndCategoryAndProjectIdAndAssignedToId() {
        Task task = new Task();
        task.setTaskName("Test Task");
        task.setTaskDescription("Test Description");
        task.setCategory(TaskCategory.FRONTEND);
        task.setProjectId(1L);
        task.setAssignedToId(1L);
        when(taskRepository.findByTaskNameAndTaskDescriptionAndCategoryAndProjectIdAndAssignedToId(
                "Test Task", "Test Description", TaskCategory.FRONTEND, 1L, 1L))
                .thenReturn(List.of(task));

        List<Task> result = taskRepository.findByTaskNameAndTaskDescriptionAndCategoryAndProjectIdAndAssignedToId(
                "Test Task", "Test Description", TaskCategory.FRONTEND, 1L, 1L);

        assertEquals(1, result.size());
        assertEquals(task, result.get(0));
        verify(taskRepository, times(1)).findByTaskNameAndTaskDescriptionAndCategoryAndProjectIdAndAssignedToId(
                "Test Task", "Test Description", TaskCategory.FRONTEND, 1L, 1L);

    }

    @Test
    public void testFindByTaskNameAndTaskDescriptionAndProjectIdAndCategory() {
        Task task = new Task();
        task.setTaskName("Test Task");
        task.setTaskDescription("Test Description");
        task.setProjectId(1L);
        task.setCategory(TaskCategory.FRONTEND);
        when(taskRepository.findByTaskNameAndTaskDescriptionAndProjectIdAndCategory(
                "Test Task", "Test Description", 1L, TaskCategory.FRONTEND))
                .thenReturn(List.of(task));

        List<Task> result = taskRepository.findByTaskNameAndTaskDescriptionAndProjectIdAndCategory(
                "Test Task", "Test Description", 1L, TaskCategory.FRONTEND);

        assertEquals(1, result.size());
        assertEquals(task, result.get(0));
        verify(taskRepository, times(1)).findByTaskNameAndTaskDescriptionAndProjectIdAndCategory(
                "Test Task", "Test Description", 1L, TaskCategory.FRONTEND);

    }

    @Test
    public void testFindByTaskNameAndTaskDescriptionAndProjectIdAndCategoryNoResult() {
        when(taskRepository.findByTaskNameAndTaskDescriptionAndProjectIdAndCategory(
                "Test Task", "Test Description", 1L, TaskCategory.FRONTEND))
                .thenReturn(List.of());

        List<Task> result = taskRepository.findByTaskNameAndTaskDescriptionAndProjectIdAndCategory(
                "Test Task", "Test Description", 1L, TaskCategory.FRONTEND);

        assertTrue(result.isEmpty());
        verify(taskRepository, times(1)).findByTaskNameAndTaskDescriptionAndProjectIdAndCategory(
                "Test Task", "Test Description", 1L, TaskCategory.FRONTEND);

    }
    @Test
    public void testSearchTasksByName() {
        Task task = new Task();
        task.setTaskName("Test Task");
        when(taskRepository.searchTasks("Test")).thenReturn(List.of(task));
        List<Task> tasks = taskRepository.searchTasks("Test");
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTaskName());
    }

    @Test
    public void testSearchTasksByDescription() {
        Task task = new Task();
        task.setTaskDescription("Test Description");
        when(taskRepository.searchTasks("Test Description")).thenReturn(List.of(task));
        List<Task> tasks = taskRepository.searchTasks("Test Description");
        assertEquals(1, tasks.size());
        assertEquals("Test Description", tasks.get(0).getTaskDescription());
    }

    @Test
    public void testSearchTasksByProjectId() {
        Task task = new Task();
        task.setProjectId(1L);
        when(taskRepository.searchTasks("1")).thenReturn(List.of(task));
        List<Task> tasks = taskRepository.searchTasks("1");
        assertEquals(1, tasks.size());
        assertEquals(1L, tasks.get(0).getProjectId().longValue());
    }
    @Test
    public void testSearchTasksByCategory() {
        Task task = new Task();
        task.setCategory(TaskCategory.FRONTEND);
        when(taskRepository.searchTasks("FRONTEND")).thenReturn(List.of(task));
        List<Task> tasks = taskRepository.searchTasks("FRONTEND");
        assertEquals(1, tasks.size());
        assertEquals(TaskCategory.FRONTEND, tasks.get(0).getCategory());
    }

    @Test
    public void testSearchTasksMultipleResults() {
        Task task1 = new Task();
        task1.setTaskName("Test Task 1");
        Task task2 = new Task();
        task2.setTaskName("Test Task 2");
        when(taskRepository.searchTasks("Test")).thenReturn(List.of(task1, task2));
        List<Task> tasks = taskRepository.searchTasks("Test");
        assertEquals(2, tasks.size());
    }

    @Test
    public void testSearchTasksNoResults() {
        when(taskRepository.searchTasks("Non-existent task")).thenReturn(List.of());
        List<Task> tasks = taskRepository.searchTasks("Non-existent task");
        assertTrue(tasks.isEmpty());
    }



}

package com.taskhub.taskmanagement.service;


import com.taskhub.taskmanagement.entity.Task;
import com.taskhub.taskmanagement.entity.TaskStatus;
import com.taskhub.taskmanagement.exception.InvalidTaskException;
import com.taskhub.taskmanagement.exception.TaskAlreadyExistsException;
import com.taskhub.taskmanagement.exception.TaskNotFoundException;
import com.taskhub.taskmanagement.repository.TaskRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long taskId) {
        if (taskId == null) {
            throw new InvalidTaskException("Task ID is required");
        }
        return taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found with ID " + taskId));
    }

    public Task createTask(Task task) {
        if (task == null) {
            throw new InvalidTaskException("Task is required");
        }
            List<Task> existingTasks = taskRepository.findByTaskNameAndTaskDescriptionAndProjectIdAndCategory(
                    task.getTaskName(), task.getTaskDescription(), task.getProjectId(), task.getCategory());
            if (!existingTasks.isEmpty()) {
                throw new TaskAlreadyExistsException("Task with same name, description, project ID, and category already exists.");
            }
            return taskRepository.save(task);
    }

    public Task updateTask(Task task) {
        if (task == null || task.getTaskId() == null) {
            throw new InvalidTaskException("Task ID is required");
        }
            Task existingTask = taskRepository.findById(task.getTaskId()).orElseThrow(() -> new TaskNotFoundException("Task not found with ID " + task.getTaskId()));
            existingTask.setTaskName(task.getTaskName());
            existingTask.setTaskDescription(task.getTaskDescription());
            existingTask.setProjectId(task.getProjectId());
            existingTask.setAssignedTo(task.getAssignedTo());
            existingTask.setStatus(task.getStatus());
            existingTask.setPriority(task.getPriority());
            existingTask.setDueDate(task.getDueDate());
            existingTask.setCategory(task.getCategory());
            return taskRepository.save(existingTask);
    }
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }


    public void deleteTask(Long taskId) {
        if (taskId == null) {
            throw new InvalidTaskException("Task ID is required");
        }
        if (taskRepository.findById(taskId).isEmpty()) {
            throw new TaskNotFoundException("There is no task with id: " + taskId);
        }
        taskRepository.deleteById(taskId);

    }
    public Object getTasks(String query, HttpServletRequest request, Model model) {
        List<Task> tasks;
        if (query != null && !query.isEmpty()) {
            tasks = searchTasks(query);
        } else {
            tasks = getAllTasks();
        }

        if (request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json")) {
            Map<String, Object> response = new HashMap<>();
            response.put("tasks", tasks);
            response.put("doneCount", tasks.stream().filter(task -> task.getStatus() == TaskStatus.DONE).count());
            response.put("inProgressCount", tasks.stream().filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS).count());
            response.put("todoCount", tasks.stream().filter(task -> task.getStatus() == TaskStatus.TODO).count());
            response.put("totalCount", tasks.size());
            return ResponseEntity.ok(response);
        } else {
            model.addAttribute("tasks", tasks);
            int doneCount = (int) tasks.stream().filter(task -> task.getStatus() == TaskStatus.DONE).count();
            int inProgressCount = (int) tasks.stream().filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS).count();
            int todoCount = (int) tasks.stream().filter(task -> task.getStatus() == TaskStatus.TODO).count();
            model.addAttribute("doneCount", doneCount);
            model.addAttribute("inProgressCount", inProgressCount);
            model.addAttribute("todoCount", todoCount);
            model.addAttribute("totalCount", tasks.size());
            return "tasks";
        }
    }

    public List<Task> searchTasks(String query) {
        if (query == null || query.isEmpty()) {
            throw new InvalidTaskException("Search query is required");
        }

            return taskRepository.searchTasks(query);
    }



    
}

package com.taskhub.taskmanagement.controller;

import com.taskhub.taskmanagement.entity.TaskStatus;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.taskhub.taskmanagement.entity.Task;
import com.taskhub.taskmanagement.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Operation(summary = "get task by its id")
    @GetMapping("/{taskId}")
    public String getTaskById(@Parameter(description = "Id of the task to retrieve")@PathVariable Long taskId, Model model) {
        model.addAttribute("task", taskService.getTaskById(taskId));
        return "task";
    }
    @GetMapping("/tasks")
    public String getAllTasks(Model model) {
        List<Task> tasks = taskService.getAllTasks();
        model.addAttribute("tasks", tasks);
        return "tasks";
    }


    @GetMapping("/create")
    public String createTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "create-task";
    }
    @Operation(summary = "create a task")
    @PostMapping("/create")
    public String createTask(@Valid @ModelAttribute Task task, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("task", task);
            return "create-task";
        }
           taskService.createTask(task);
        return "redirect:/tasks";
           }

    @GetMapping("/update/{taskId}")
    public String updateTaskForm(@Parameter(description = "Id of the task to update")@PathVariable Long taskId, Model model) {
        model.addAttribute("task", taskService.getTaskById(taskId));
        return "update-task";
    }
    @Operation(summary = "update a task")
    @PostMapping("/update/{taskId}")
    public String updateTask(@Parameter(description = "Id of the task to update")@PathVariable Long taskId,@Valid @ModelAttribute Task task,Model model,BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("task", task);
            return "update-task";
        }
        if (task == null|| task.getTaskName() == null) {
            return "redirect:/tasks"; // or return an error view
        }
            taskService.updateTask(task);
            return "redirect:/tasks";

    }
    @Operation(summary = "delete a task")
    @GetMapping("/delete/{taskId}")
    public String deleteTask(@Parameter(description = "Id of the task to delete")@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
        return "redirect:/tasks";
    }
    @Operation(summary = "get all todo tasks")
    @GetMapping("/status/todo")
    public String getTodoTasks(Model model) {
        List<Task> tasks = taskService.getTasksByStatus(TaskStatus.TODO);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }
    @Operation(summary = "get all tasks which are in progress")
    @GetMapping("/status/in-progress")
    public String getInProgressTasks(Model model) {
        List<Task> tasks = taskService.getTasksByStatus(TaskStatus.IN_PROGRESS);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }
    @Operation(summary = "get list of all completed tasks")
    @GetMapping("/status/done")
    public String getDoneTasks(Model model) {
        List<Task> tasks = taskService.getTasksByStatus(TaskStatus.DONE);
        model.addAttribute("tasks", tasks);
        return "tasks";
    }


    @Operation(summary = "get all tasks or get task based on search ")
    @GetMapping
    public String getTasks(@Parameter(description = "Search query")@RequestParam(required = false) String query, Model model) {
        List<Task> tasks;
        if(query != null && !query.isEmpty()) {
            tasks = taskService.searchTasks(query);
        } else {
            tasks = taskService.getAllTasks();

        }
        model.addAttribute("tasks", tasks);
        int doneCount = 0;
        int inProgressCount = 0;
        int todoCount = 0;
        for (Task task : tasks) {
            if (task.getStatus()== TaskStatus.DONE) {
                doneCount++;
            } else if (task.getStatus()== TaskStatus.IN_PROGRESS) {
                inProgressCount++;
            } else if (task.getStatus()== TaskStatus.TODO) {
                todoCount++;
            }
        }
        model.addAttribute("doneCount", doneCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("todoCount", todoCount);
        model.addAttribute("totalCount", tasks.size());
        return "tasks";
    }



}

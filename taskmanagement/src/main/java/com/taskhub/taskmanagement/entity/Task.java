package com.taskhub.taskmanagement.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Schema(description = "Task entity")
public class Task {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;
    @NotBlank(message = "Task name is required")
    @Size(min = 10, message = "Task name should be at least 10 characters long")
    @Pattern(regexp = "^[a-zA-Z0-9\\s.,!?\\-]+$", message = "Task name should contain only letters, numbers, spaces, and basic punctuation")
    private String taskName;
    @NotBlank(message = "Task description is required")
    @Size(min = 1000,max = 2000, message = "Task description should be between 1000 and 2000 characters long")
    @Column(length = 2000)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[\\\\w\\\\s.,!?*\\\\\\\"])[a-zA-Z0-9\\s.,!?*\\\"\\-]{5,}$", message = "Task description should only letters, numbers, spaces, and basic punctuation")
    private String taskDescription;
    @NotNull(message = "Project ID is required")
    private Long projectId; //Foreign key for project tabe
    @NotNull(message = "Creator ID is required")
    private Long creatorId;//Foreign key for the user table
    @NotNull(message = "Assigned to ID is required")
    private Long assignedToId;  // FK to User
    @NotBlank(message = "name of the user to whom the task is assigned is required")
    private String assignedTo;
    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    @NotNull(message = "Priority is required")
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    private TaskCategory category;
    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date should be present or future date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    @NotBlank(message = "Created by is required")
    private String createdBy;

    public Long getAssignedToId() {
        return assignedToId;
    }

    public void setAssignedToId(Long assignedToId) {
        this.assignedToId = assignedToId;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public Long getProjectId() {
        return projectId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }



    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public TaskCategory getCategory() {
        return category;
    }

    public void setCategory(TaskCategory category) {
        this.category = category;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }



}

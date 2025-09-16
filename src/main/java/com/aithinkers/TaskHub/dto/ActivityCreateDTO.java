package com.aithinkers.TaskHub.dto;

import com.aithinkers.TaskHub.entity.Activity;
import com.aithinkers.TaskHub.enums.ActionType;
import com.aithinkers.TaskHub.enums.Priority;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// DTO for creating new activities
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityCreateDTO {

    @NotNull(message = "Task ID is required")
    private Long taskId;

    @NotNull(message = "Action type is required")
    private ActionType actionType;

    @Size(max = 500, message = "Action details cannot exceed 500 characters")
    private String actionDetails;

    @NotNull(message = "Performed by is required")
    private Long performedBy;

    @NotNull(message = "Priority is required")
    private Priority priority;
}





package com.aithinkers.TaskHub.dto;

import com.aithinkers.TaskHub.entity.Activity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO for returning activity data
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityResponseDTO {

    private Long id;
    private Long taskId;
    private Activity.ActionType actionType;
    private String actionDetails;
    private Long performedBy;
    private Activity.Priority priority;
    private LocalDateTime timestamp;
}
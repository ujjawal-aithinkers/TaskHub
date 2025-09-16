package com.aithinkers.TaskHub.dto;


import com.aithinkers.TaskHub.entity.Activity;
import com.aithinkers.TaskHub.enums.ActionType;
import com.aithinkers.TaskHub.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivitySummaryDTO {

    private Long id;
    private Long taskId;
    private ActionType actionType;
    private String actionDetails;
    private Priority priority;
    private LocalDateTime timestamp;
}
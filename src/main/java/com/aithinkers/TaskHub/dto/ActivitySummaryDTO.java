package com.aithinkers.TaskHub.dto;


import com.aithinkers.TaskHub.entity.Activity;
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
    private Activity.ActionType actionType;
    private String actionDetails;
    private Activity.Priority priority;
    private LocalDateTime timestamp;
}
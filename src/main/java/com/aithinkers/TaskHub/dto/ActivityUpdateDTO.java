package com.aithinkers.TaskHub.dto;

import com.aithinkers.TaskHub.entity.Activity;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityUpdateDTO {

    private Activity.ActionType actionType;

    @Size(max = 500, message = "Action details cannot exceed 500 characters")
    private String actionDetails;

    private Activity.Priority priority;
}
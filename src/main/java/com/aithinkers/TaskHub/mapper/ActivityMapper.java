package com.aithinkers.TaskHub.mapper;

import com.aithinkers.TaskHub.dto.ActivityCreateDTO;
import com.aithinkers.TaskHub.dto.ActivityResponseDTO;
import com.aithinkers.TaskHub.dto.ActivitySummaryDTO;
import com.aithinkers.TaskHub.dto.ActivityUpdateDTO;
import com.aithinkers.TaskHub.entity.Activity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ActivityMapper {

    // Convert CreateDTO to Entity
    public Activity toEntity(ActivityCreateDTO dto) {
        return Activity.builder()
                .taskId(dto.getTaskId())
                .actionType(dto.getActionType())
                .actionDetails(dto.getActionDetails())
                .performedBy(dto.getPerformedBy())
                .priority(dto.getPriority())
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Convert Entity to ResponseDTO
    public ActivityResponseDTO toResponseDTO(Activity entity) {
        return ActivityResponseDTO.builder()
                .id(entity.getId())
                .taskId(entity.getTaskId())
                .actionType(entity.getActionType())
                .actionDetails(entity.getActionDetails())
                .performedBy(entity.getPerformedBy())
                .priority(entity.getPriority())
                .timestamp(entity.getTimestamp())
                .build();
    }

    // Convert Entity to SummaryDTO
    public ActivitySummaryDTO toSummaryDTO(Activity entity) {
        return ActivitySummaryDTO.builder()
                .id(entity.getId())
                .taskId(entity.getTaskId())
                .actionType(entity.getActionType())
                .actionDetails(entity.getActionDetails())
                .priority(entity.getPriority())
                .timestamp(entity.getTimestamp())
                .build();
    }

    // Update entity with UpdateDTO
    public void updateEntity(Activity entity, ActivityUpdateDTO dto) {
        if (dto.getActionType() != null) {
            entity.setActionType(dto.getActionType());
        }
        if (dto.getActionDetails() != null) {
            entity.setActionDetails(dto.getActionDetails());
        }
        if (dto.getPriority() != null) {
            entity.setPriority(dto.getPriority());
        }
    }

    // Convert list of entities to ResponseDTOs
    public List<ActivityResponseDTO> toResponseDTOList(List<Activity> entities) {
        return entities.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Convert list of entities to SummaryDTOs
    public List<ActivitySummaryDTO> toSummaryDTOList(List<Activity> entities) {
        return entities.stream()
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }
}
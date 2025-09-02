package com.aithinkers.TaskHub.mapper;

import com.aithinkers.TaskHub.dto.ActivityCreateDTO;
import com.aithinkers.TaskHub.entity.Activity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ActivityMapperTest {

    private final ActivityMapper mapper = new ActivityMapper();

    @Test
    void toEntity_mapsFieldsCorrectly() {
        ActivityCreateDTO dto = ActivityCreateDTO.builder()
                .taskId(1L)
                .actionType(Activity.ActionType.CREATED)
                .actionDetails("Task created: T1")
                .performedBy(99L)
                .priority(Activity.Priority.MEDIUM)
                .build();

        Activity entity = mapper.toEntity(dto);

        assertThat(entity.getTaskId()).isEqualTo(1L);
        assertThat(entity.getActionType()).isEqualTo(Activity.ActionType.CREATED);
        assertThat(entity.getActionDetails()).isEqualTo("Task created: T1");
        assertThat(entity.getPerformedBy()).isEqualTo(99L);
        assertThat(entity.getPriority()).isEqualTo(Activity.Priority.MEDIUM);
        assertThat(entity.getTimestamp()).isNotNull();
    }
}

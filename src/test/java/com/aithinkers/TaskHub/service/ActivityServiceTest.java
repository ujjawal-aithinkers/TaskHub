package com.aithinkers.TaskHub.service;

import com.aithinkers.TaskHub.Service.ActivityService;
import com.aithinkers.TaskHub.dto.ActivityCreateDTO;
import com.aithinkers.TaskHub.dto.ActivityResponseDTO;
import com.aithinkers.TaskHub.dto.ActivityUpdateDTO;
import com.aithinkers.TaskHub.entity.Activity;
import com.aithinkers.TaskHub.enums.ActionType;
import com.aithinkers.TaskHub.enums.Priority;
import com.aithinkers.TaskHub.exception.ResourceNotFoundException;
import com.aithinkers.TaskHub.mapper.ActivityMapper;
import com.aithinkers.TaskHub.repository.ActivityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    ActivityRepository repo;

    @Spy
    ActivityMapper mapper = new ActivityMapper();

    @InjectMocks
    ActivityService service;

    @Test
    void createActivity_saves_and_returnsDTO() {
        ActivityCreateDTO dto = ActivityCreateDTO.builder()
                .taskId(1L).actionType(ActionType.CREATED)
                .actionDetails("Task created: T1")
                .performedBy(7L).priority(Priority.MEDIUM)
                .build();

        Activity saved = Activity.builder()
                .id(10L).taskId(1L).actionType(ActionType.CREATED)
                .actionDetails("Task created: T1").performedBy(7L)
                .priority(Priority.MEDIUM).timestamp(LocalDateTime.now())
                .build();

        when(repo.save(any(Activity.class))).thenReturn(saved);

        ActivityResponseDTO resp = service.createActivity(dto);

        assertThat(resp.getId()).isEqualTo(10L);
        verify(repo, times(1)).save(any(Activity.class));
    }

    @Test
    void updateActivity_updatesFields_whenFound() {
        Activity existing = Activity.builder()
                .id(5L).taskId(99L).actionType(ActionType.CREATED)
                .actionDetails("old").performedBy(1L)
                .priority(Priority.LOW).timestamp(LocalDateTime.now())
                .build();

        when(repo.findById(5L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Activity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActivityUpdateDTO upd = ActivityUpdateDTO.builder()
                .actionType(ActionType.UPDATED).actionDetails("new")
                .priority(Priority.HIGH).build();

        ActivityResponseDTO dto = service.updateActivity(5L, upd);

        assertThat(dto.getActionType()).isEqualTo(ActionType.UPDATED);
        assertThat(dto.getActionDetails()).isEqualTo("new");
        assertThat(dto.getPriority()).isEqualTo(Priority.HIGH);
        verify(repo).save(any(Activity.class));
    }

    @Test
    void updateActivity_throws_whenNotFound() {
        when(repo.findById(5L)).thenReturn(Optional.empty());

        ActivityUpdateDTO upd = ActivityUpdateDTO.builder()
                .actionType(ActionType.UPDATED).actionDetails("new")
                .priority(Priority.HIGH).build();

        assertThatThrownBy(() -> service.updateActivity(5L, upd))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Activity not found");
    }

    @Test
    void deleteActivity_deletes_whenExists() {
        when(repo.existsById(1L)).thenReturn(true);

        service.deleteActivity(1L);

        verify(repo).deleteById(1L);
    }

    @Test
    void deleteActivity_throws_whenNotFound() {
        when(repo.existsById(2L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteActivity(2L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getLatestActivityForTask_returnsDTO_whenFound() {
        Activity latest = Activity.builder()
                .id(42L).taskId(9L).actionType(ActionType.COMPLETED)
                .actionDetails("done").performedBy(2L).priority(Priority.HIGH)
                .timestamp(LocalDateTime.now()).build();

        when(repo.findTopByTaskIdOrderByTimestampDesc(9L)).thenReturn(Optional.of(latest));

        ActivityResponseDTO dto = service.getLatestActivityForTask(9L);

        assertThat(dto.getId()).isEqualTo(42L);
        assertThat(dto.getActionDetails()).isEqualTo("done");
    }

    @Test
    void getLatestActivityForTask_throws_whenNotFound() {
        when(repo.findTopByTaskIdOrderByTimestampDesc(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getLatestActivityForTask(9L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getActivityCountForTask_delegatesToRepo() {
        when(repo.countByTaskId(5L)).thenReturn(7L);

        long count = service.getActivityCountForTask(5L);

        assertThat(count).isEqualTo(7L);
    }
}

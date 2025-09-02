package com.aithinkers.TaskHub.service;

import com.aithinkers.TaskHub.Service.ActivityService;
import com.aithinkers.TaskHub.dto.ActivityCreateDTO;
import com.aithinkers.TaskHub.dto.ActivityResponseDTO;
import com.aithinkers.TaskHub.dto.ActivityUpdateDTO;
import com.aithinkers.TaskHub.entity.Activity;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest {

    @Mock
    ActivityRepository repo;

    // Real mapper (no Spring) so mapping logic is exercised
    @Spy
    ActivityMapper mapper = new ActivityMapper();

    @InjectMocks
    ActivityService service;

    @Test
    void createActivity_saves_and_returnsDTO() {
        // arrange
        ActivityCreateDTO dto = ActivityCreateDTO.builder()
                .taskId(1L)
                .actionType(Activity.ActionType.CREATED)
                .actionDetails("Task created: T1")
                .performedBy(7L)
                .priority(Activity.Priority.MEDIUM)
                .build();

        Activity saved = Activity.builder()
                .id(10L)
                .taskId(1L)
                .actionType(Activity.ActionType.CREATED)
                .actionDetails("Task created: T1")
                .performedBy(7L)
                .priority(Activity.Priority.MEDIUM)
                .timestamp(LocalDateTime.now())
                .build();

        when(repo.save(any(Activity.class))).thenReturn(saved);

        // act
        ActivityResponseDTO resp = service.createActivity(dto);

        // assert
        assertThat(resp.getId()).isEqualTo(10L);
        assertThat(resp.getTaskId()).isEqualTo(1L);
        verify(repo, times(1)).save(any(Activity.class));
    }

    @Test
    void updateActivity_updatesFields_whenFound() {
        Activity existing = Activity.builder()
                .id(5L)
                .taskId(99L)
                .actionType(Activity.ActionType.CREATED)
                .actionDetails("old")
                .performedBy(1L)
                .priority(Activity.Priority.LOW)
                .timestamp(LocalDateTime.now())
                .build();

        when(repo.findById(5L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Activity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ActivityUpdateDTO upd = ActivityUpdateDTO.builder()
                .actionType(Activity.ActionType.UPDATED)
                .actionDetails("new")
                .priority(Activity.Priority.HIGH)
                .build();

        Optional<ActivityResponseDTO> result = service.updateActivity(5L, upd);

        assertThat(result).isPresent();
        ActivityResponseDTO dto = result.get();
        assertThat(dto.getActionType()).isEqualTo(Activity.ActionType.UPDATED);
        assertThat(dto.getActionDetails()).isEqualTo("new");
        assertThat(dto.getPriority()).isEqualTo(Activity.Priority.HIGH);
        verify(repo).save(any(Activity.class));
    }

    @Test
    void deleteActivity_returnsTrue_whenExists_otherwiseFalse() {
        when(repo.existsById(1L)).thenReturn(true);
        boolean ok = service.deleteActivity(1L);
        assertThat(ok).isTrue();
        verify(repo).deleteById(1L);

        when(repo.existsById(2L)).thenReturn(false);
        boolean notOk = service.deleteActivity(2L);
        assertThat(notOk).isFalse();
        verify(repo, never()).deleteById(2L);
    }

    @Test
    void getActivitiesByTaskPaged_returnsMappedPage() {
        Long taskId = 123L;
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "timestamp"));

        Activity a1 = Activity.builder().id(1L).taskId(taskId)
                .actionType(Activity.ActionType.CREATED).actionDetails("a1")
                .performedBy(1L).priority(Activity.Priority.MEDIUM)
                .timestamp(LocalDateTime.now()).build();

        Activity a2 = Activity.builder().id(2L).taskId(taskId)
                .actionType(Activity.ActionType.UPDATED).actionDetails("a2")
                .performedBy(1L).priority(Activity.Priority.HIGH)
                .timestamp(LocalDateTime.now()).build();

        when(repo.findByTaskId(eq(taskId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(a1, a2), pageable, 2));

        var page = service.getActivitiesByTaskPaged(taskId, 0, 2);

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent().get(0).getTaskId()).isEqualTo(taskId);
        // ensure mapping happened (DTO, not entity)
        assertThat(page.getContent().get(0).getActionDetails()).isEqualTo("a1");
    }

    @Test
    void getLatestActivityForTask_mapsOptionalCorrectly() {
        Activity latest = Activity.builder()
                .id(42L).taskId(9L)
                .actionType(Activity.ActionType.COMPLETED)
                .actionDetails("done")
                .performedBy(2L)
                .priority(Activity.Priority.HIGH)
                .timestamp(LocalDateTime.now()).build();

        when(repo.findTopByTaskIdOrderByTimestampDesc(9L)).thenReturn(Optional.of(latest));

        var dtoOpt = service.getLatestActivityForTask(9L);

        assertThat(dtoOpt).isPresent();
        assertThat(dtoOpt.get().getId()).isEqualTo(42L);
    }

    @Test
    void getActivityCountForTask_delegatesToRepo() {
        when(repo.countByTaskId(5L)).thenReturn(7L);
        assertThat(service.getActivityCountForTask(5L)).isEqualTo(7L);
    }
}

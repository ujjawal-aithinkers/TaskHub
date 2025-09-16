package com.aithinkers.TaskHub.Service;

import com.aithinkers.TaskHub.dto.ActivityCreateDTO;
import com.aithinkers.TaskHub.dto.ActivityResponseDTO;
import com.aithinkers.TaskHub.dto.ActivitySummaryDTO;
import com.aithinkers.TaskHub.dto.ActivityUpdateDTO;
import com.aithinkers.TaskHub.entity.Activity;
import com.aithinkers.TaskHub.enums.ActionType;
import com.aithinkers.TaskHub.enums.Priority;
import com.aithinkers.TaskHub.exception.ResourceNotFoundException;
import com.aithinkers.TaskHub.mapper.ActivityMapper;
import com.aithinkers.TaskHub.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository repository;
    private final ActivityMapper mapper;

    // CREATE operations
    @Transactional
    public ActivityResponseDTO createActivity(ActivityCreateDTO createDTO) {
        if (createDTO == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (createDTO.getTaskId() == null) {
            throw new IllegalArgumentException("taskId is required");
        }
        if (createDTO.getPerformedBy() == null) {
            throw new IllegalArgumentException("performedBy is required");
        }
        try {
            Activity activity = mapper.toEntity(createDTO);
            Activity savedActivity = repository.save(activity);
            return mapper.toResponseDTO(savedActivity);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Database constraint violation when creating activity: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }

    @Transactional
    public List<ActivityResponseDTO> createActivities(List<ActivityCreateDTO> createDTOs) {
        if (createDTOs == null || createDTOs.isEmpty()) {
            throw new IllegalArgumentException("Request body must contain at least one activity");
        }
        try {
            List<Activity> activities = createDTOs.stream()
                    .map(mapper::toEntity)
                    .toList();
            List<Activity> savedActivities = repository.saveAll(activities);
            return mapper.toResponseDTOList(savedActivities);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Database constraint violation when creating activities: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }

    // Quick logging methods
    @Transactional
    public ActivityResponseDTO logTaskCreated(Long taskId, Long createdBy, String taskTitle) {
        if (taskId == null || createdBy == null) {
            throw new IllegalArgumentException("taskId and createdBy are required");
        }
        ActivityCreateDTO createDTO = ActivityCreateDTO.builder()
                .taskId(taskId)
                .actionType(ActionType.CREATED)
                .actionDetails("Task created: " + (taskTitle == null ? "" : taskTitle))
                .performedBy(createdBy)
                .priority(Priority.MEDIUM)
                .build();
        return createActivity(createDTO);
    }

    @Transactional
    public ActivityResponseDTO logTaskUpdated(Long taskId, Long updatedBy, String updateDetails) {
        if (taskId == null || updatedBy == null) {
            throw new IllegalArgumentException("taskId and updatedBy are required");
        }
        ActivityCreateDTO createDTO = ActivityCreateDTO.builder()
                .taskId(taskId)
                .actionType(ActionType.UPDATED)
                .actionDetails("Task updated: " + (updateDetails == null ? "" : updateDetails))
                .performedBy(updatedBy)
                .priority(Priority.LOW)
                .build();
        return createActivity(createDTO);
    }

    @Transactional
    public ActivityResponseDTO logTaskCompleted(Long taskId, Long completedBy, String completionDetails) {
        if (taskId == null || completedBy == null) {
            throw new IllegalArgumentException("taskId and completedBy are required");
        }
        ActivityCreateDTO createDTO = ActivityCreateDTO.builder()
                .taskId(taskId)
                .actionType(ActionType.COMPLETED)
                .actionDetails("Task completed: " + (completionDetails == null ? "" : completionDetails))
                .performedBy(completedBy)
                .priority(Priority.HIGH)
                .build();
        return createActivity(createDTO);
    }

    // READ operations
    public List<ActivitySummaryDTO> getAllActivities() {
        List<Activity> activities = repository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
        return mapper.toSummaryDTOList(activities);
    }

    public Page<ActivitySummaryDTO> getAllActivitiesPaged(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("page must be >= 0 and size must be > 0");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<Activity> activities = repository.findAll(pageable);
        return activities.map(mapper::toSummaryDTO);
    }

    public ActivityResponseDTO getActivityById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }
        return repository.findById(id)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Activity not found with id: " + id));
    }

    public List<ActivitySummaryDTO> getActivitiesByTask(Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is required");
        }
        List<Activity> activities = repository.findByTaskIdOrderByTimestampDesc(taskId);
        return mapper.toSummaryDTOList(activities);
    }

    public Page<ActivitySummaryDTO> getActivitiesByTaskPaged(Long taskId, int page, int size) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is required");
        }
        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException("page must be >= 0 and size must be > 0");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<Activity> activities = repository.findByTaskId(taskId, pageable);
        return activities.map(mapper::toSummaryDTO);
    }

    public List<ActivitySummaryDTO> getActivitiesByUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }
        List<Activity> activities = repository.findByPerformedByOrderByTimestampDesc(userId);
        return mapper.toSummaryDTOList(activities);
    }

    public List<ActivitySummaryDTO> getRecentActivities(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be > 0");
        }
        List<Activity> activities = repository.findTop10ByOrderByTimestampDesc();
        return mapper.toSummaryDTOList(activities);
    }

    public List<ActivitySummaryDTO> getRecentActivitiesForTask(Long taskId, int hoursBack) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is required");
        }
        if (hoursBack <= 0) {
            throw new IllegalArgumentException("hoursBack must be > 0");
        }
        LocalDateTime since = LocalDateTime.now().minusHours(hoursBack);
        List<Activity> activities = repository.findRecentActivitiesForTask(taskId, since);
        return mapper.toSummaryDTOList(activities);
    }

    public ActivityResponseDTO getLatestActivityForTask(Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is required");
        }
        return repository.findTopByTaskIdOrderByTimestampDesc(taskId)
                .map(mapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("No activity found for taskId: " + taskId));
    }

    public List<ActivitySummaryDTO> getActivitiesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate and endDate are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must be after startDate");
        }
        List<Activity> activities = repository.findByTimestampBetween(startDate, endDate);
        return mapper.toSummaryDTOList(activities);
    }

    public List<ActivitySummaryDTO> searchActivities(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("keyword is required");
        }
        List<Activity> activities = repository.findByActionDetailsContaining(keyword);
        return mapper.toSummaryDTOList(activities);
    }

    // Statistics
    public long getActivityCountForTask(Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is required");
        }
        return repository.countByTaskId(taskId);
    }

    public long getActivityCountForUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }
        return repository.countByPerformedBy(userId);
    }

    // UPDATE operations
    @Transactional
    public ActivityResponseDTO updateActivity(Long id, ActivityUpdateDTO updateDTO) {
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }
        if (updateDTO == null) {
            throw new IllegalArgumentException("update body is required");
        }
        try {
            return repository.findById(id)
                    .map(activity -> {
                        mapper.updateEntity(activity, updateDTO);
                        Activity savedActivity = repository.save(activity);
                        return mapper.toResponseDTO(savedActivity);
                    })
                    .orElseThrow(() -> new ResourceNotFoundException("Cannot update. Activity not found with id: " + id));
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Database constraint violation when updating activity: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }

    // DELETE operations
    @Transactional
    public void deleteActivity(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("id is required");
        }
        try {
            if (!repository.existsById(id)) {
                throw new ResourceNotFoundException("Cannot delete. Activity not found with id: " + id);
            }
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ResourceNotFoundException("Cannot delete. Activity not found with id: " + id);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Database constraint violation when deleting activity: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }

    @Transactional
    public void deleteActivitiesByTask(Long taskId) {
        if (taskId == null) {
            throw new IllegalArgumentException("taskId is required");
        }
        try {
            repository.deleteByTaskId(taskId);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Database constraint violation when deleting activities by task: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }

    @Transactional
    public void deleteActivitiesByUser(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }
        try {
            repository.deleteByPerformedBy(userId);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Database constraint violation when deleting activities by user: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }

    @Transactional
    public void deleteOldActivities(int daysToKeep) {
        if (daysToKeep <= 0) {
            throw new IllegalArgumentException("daysToKeep must be > 0");
        }
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
            repository.deleteByTimestampBefore(cutoffDate);
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Database constraint violation when cleaning up activities: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }

    @Transactional
    public void deleteAllActivities() {
        try {
            repository.deleteAll();
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalArgumentException("Database constraint violation when deleting all activities: " +
                    (ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage()));
        }
    }

    public long getTotalActivityCount() {
        return repository.count();
    }
}

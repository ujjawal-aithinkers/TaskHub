package com.aithinkers.TaskHub.Service;

import com.aithinkers.TaskHub.dto.ActivityCreateDTO;
import com.aithinkers.TaskHub.dto.ActivityResponseDTO;
import com.aithinkers.TaskHub.dto.ActivitySummaryDTO;
import com.aithinkers.TaskHub.dto.ActivityUpdateDTO;
import com.aithinkers.TaskHub.entity.Activity;
import com.aithinkers.TaskHub.mapper.ActivityMapper;
import com.aithinkers.TaskHub.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository repository;
    private final ActivityMapper mapper;

    // CREATE operations
    @Transactional
    public ActivityResponseDTO createActivity(ActivityCreateDTO createDTO) {
        Activity activity = mapper.toEntity(createDTO);
        Activity savedActivity = repository.save(activity);
        return mapper.toResponseDTO(savedActivity);
    }

    @Transactional
    public List<ActivityResponseDTO> createActivities(List<ActivityCreateDTO> createDTOs) {
        List<Activity> activities = createDTOs.stream()
                .map(mapper::toEntity)
                .toList();
        List<Activity> savedActivities = repository.saveAll(activities);
        return mapper.toResponseDTOList(savedActivities);
    }

    // Quick logging methods for common actions
    @Transactional
    public ActivityResponseDTO logTaskCreated(Long taskId, Long createdBy, String taskTitle) {
        ActivityCreateDTO createDTO = ActivityCreateDTO.builder()
                .taskId(taskId)
                .actionType(Activity.ActionType.CREATED)
                .actionDetails("Task created: " + taskTitle)
                .performedBy(createdBy)
                .priority(Activity.Priority.MEDIUM)
                .build();
        return createActivity(createDTO);
    }

    @Transactional
    public ActivityResponseDTO logTaskUpdated(Long taskId, Long updatedBy, String updateDetails) {
        ActivityCreateDTO createDTO = ActivityCreateDTO.builder()
                .taskId(taskId)
                .actionType(Activity.ActionType.UPDATED)
                .actionDetails("Task updated: " + updateDetails)
                .performedBy(updatedBy)
                .priority(Activity.Priority.LOW)
                .build();
        return createActivity(createDTO);
    }

    @Transactional
    public ActivityResponseDTO logTaskCompleted(Long taskId, Long completedBy, String completionDetails) {
        ActivityCreateDTO createDTO = ActivityCreateDTO.builder()
                .taskId(taskId)
                .actionType(Activity.ActionType.COMPLETED)
                .actionDetails("Task completed: " + completionDetails)
                .performedBy(completedBy)
                .priority(Activity.Priority.HIGH)
                .build();
        return createActivity(createDTO);
    }

    // READ operations
    public List<ActivitySummaryDTO> getAllActivities() {
        List<Activity> activities = repository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
        return mapper.toSummaryDTOList(activities);
    }

    public Page<ActivitySummaryDTO> getAllActivitiesPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<Activity> activities = repository.findAll(pageable);
        return activities.map(mapper::toSummaryDTO);
    }

    public Optional<ActivityResponseDTO> getActivityById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponseDTO);
    }

    public List<ActivitySummaryDTO> getActivitiesByTask(Long taskId) {
        List<Activity> activities = repository.findByTaskIdOrderByTimestampDesc(taskId);
        return mapper.toSummaryDTOList(activities);
    }

    public Page<ActivitySummaryDTO> getActivitiesByTaskPaged(Long taskId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<Activity> activities = repository.findByTaskId(taskId, pageable);
        return activities.map(mapper::toSummaryDTO);
    }

    public List<ActivitySummaryDTO> getActivitiesByUser(Long userId) {
        List<Activity> activities = repository.findByPerformedByOrderByTimestampDesc(userId);
        return mapper.toSummaryDTOList(activities);
    }

    public List<ActivitySummaryDTO> getRecentActivities(int limit) {
        List<Activity> activities = repository.findTop10ByOrderByTimestampDesc();
        return mapper.toSummaryDTOList(activities);
    }

    public List<ActivitySummaryDTO> getRecentActivitiesForTask(Long taskId, int hoursBack) {
        LocalDateTime since = LocalDateTime.now().minusHours(hoursBack);
        List<Activity> activities = repository.findRecentActivitiesForTask(taskId, since);
        return mapper.toSummaryDTOList(activities);
    }

    public Optional<ActivityResponseDTO> getLatestActivityForTask(Long taskId) {
        return repository.findTopByTaskIdOrderByTimestampDesc(taskId)
                .map(mapper::toResponseDTO);
    }

    public List<ActivitySummaryDTO> getActivitiesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Activity> activities = repository.findByTimestampBetween(startDate, endDate);
        return mapper.toSummaryDTOList(activities);
    }

    public List<ActivitySummaryDTO> searchActivities(String keyword) {
        List<Activity> activities = repository.findByActionDetailsContaining(keyword);
        return mapper.toSummaryDTOList(activities);
    }

    // Statistics
    public long getActivityCountForTask(Long taskId) {
        return repository.countByTaskId(taskId);
    }

    public long getActivityCountForUser(Long userId) {
        return repository.countByPerformedBy(userId);
    }

    // UPDATE operations
    @Transactional
    public Optional<ActivityResponseDTO> updateActivity(Long id, ActivityUpdateDTO updateDTO) {
        return repository.findById(id)
                .map(activity -> {
                    mapper.updateEntity(activity, updateDTO);
                    Activity savedActivity = repository.save(activity);
                    return mapper.toResponseDTO(savedActivity);
                });
    }

    // DELETE operations
    @Transactional
    public boolean deleteActivity(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public void deleteActivitiesByTask(Long taskId) {
        repository.deleteByTaskId(taskId);
    }

    @Transactional
    public void deleteActivitiesByUser(Long userId) {
        repository.deleteByPerformedBy(userId);
    }

    @Transactional
    public void deleteOldActivities(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        repository.deleteByTimestampBefore(cutoffDate);
    }

    @Transactional
    public void deleteAllActivities() {
        repository.deleteAll();
    }

    public long getTotalActivityCount() {
        return repository.count();
    }
}
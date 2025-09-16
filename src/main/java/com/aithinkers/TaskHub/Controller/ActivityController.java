package com.aithinkers.TaskHub.Controller;

import com.aithinkers.TaskHub.dto.ActivityCreateDTO;
import com.aithinkers.TaskHub.dto.ActivityResponseDTO;
import com.aithinkers.TaskHub.dto.ActivitySummaryDTO;
import com.aithinkers.TaskHub.dto.ActivityUpdateDTO;
import com.aithinkers.TaskHub.Service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize; // commented for now
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    // CREATE endpoints
    @PostMapping
    // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ActivityResponseDTO> createActivity(@Valid @RequestBody ActivityCreateDTO createDTO) {
        ActivityResponseDTO activity = activityService.createActivity(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(activity);
    }

    @PostMapping("/batch")
    // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<ActivityResponseDTO>> createActivities(@Valid @RequestBody List<ActivityCreateDTO> createDTOs) {
        List<ActivityResponseDTO> activities = activityService.createActivities(createDTOs);
        return ResponseEntity.status(HttpStatus.CREATED).body(activities);
    }

    // Quick logging endpoints
    @PostMapping("/log/task-created")
    // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ActivityResponseDTO> logTaskCreated(
            @RequestParam Long taskId,
            @RequestParam Long createdBy,
            @RequestParam String taskTitle) {

        ActivityResponseDTO activity = activityService.logTaskCreated(taskId, createdBy, taskTitle);
        return ResponseEntity.ok(activity);
    }

    @PostMapping("/log/task-updated")
    // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ActivityResponseDTO> logTaskUpdated(
            @RequestParam Long taskId,
            @RequestParam Long updatedBy,
            @RequestParam String updateDetails) {

        ActivityResponseDTO activity = activityService.logTaskUpdated(taskId, updatedBy, updateDetails);
        return ResponseEntity.ok(activity);
    }

    @PostMapping("/log/task-completed")
    // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ActivityResponseDTO> logTaskCompleted(
            @RequestParam Long taskId,
            @RequestParam Long completedBy,
            @RequestParam String completionDetails) {

        ActivityResponseDTO activity = activityService.logTaskCompleted(taskId, completedBy, completionDetails);
        return ResponseEntity.ok(activity);
    }

    // READ endpoints (any authenticated user)
    @GetMapping
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ActivitySummaryDTO>> getAllActivities() {
        List<ActivitySummaryDTO> activities = activityService.getAllActivities();
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/paged")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ActivitySummaryDTO>> getAllActivitiesPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<ActivitySummaryDTO> activities = activityService.getAllActivitiesPaged(page, size);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/{id}")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ActivityResponseDTO> getActivityById(@PathVariable Long id) {
        ActivityResponseDTO activity = activityService.getActivityById(id);
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/task/{taskId}")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ActivitySummaryDTO>> getActivitiesByTask(@PathVariable Long taskId) {
        List<ActivitySummaryDTO> activities = activityService.getActivitiesByTask(taskId);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/task/{taskId}/paged")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<ActivitySummaryDTO>> getActivitiesByTaskPaged(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ActivitySummaryDTO> activities = activityService.getActivitiesByTaskPaged(taskId, page, size);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/user/{userId}")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ActivitySummaryDTO>> getActivitiesByUser(@PathVariable Long userId) {
        List<ActivitySummaryDTO> activities = activityService.getActivitiesByUser(userId);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/recent")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ActivitySummaryDTO>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {

        List<ActivitySummaryDTO> activities = activityService.getRecentActivities(limit);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/task/{taskId}/recent")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ActivitySummaryDTO>> getRecentActivitiesForTask(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "24") int hoursBack) {

        List<ActivitySummaryDTO> activities = activityService.getRecentActivitiesForTask(taskId, hoursBack);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/task/{taskId}/latest")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ActivityResponseDTO> getLatestActivityForTask(@PathVariable Long taskId) {
        ActivityResponseDTO activity = activityService.getLatestActivityForTask(taskId);
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/date-range")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ActivitySummaryDTO>> getActivitiesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<ActivitySummaryDTO> activities = activityService.getActivitiesByDateRange(startDate, endDate);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/search")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ActivitySummaryDTO>> searchActivities(@RequestParam String keyword) {
        List<ActivitySummaryDTO> activities = activityService.searchActivities(keyword);
        return ResponseEntity.ok(activities);
    }

    // Statistics
    @GetMapping("/task/{taskId}/count")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> getActivityCountForTask(@PathVariable Long taskId) {
        long count = activityService.getActivityCountForTask(taskId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/user/{userId}/count")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> getActivityCountForUser(@PathVariable Long userId) {
        long count = activityService.getActivityCountForUser(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @GetMapping("/count")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> getTotalActivityCount() {
        long count = activityService.getTotalActivityCount();
        return ResponseEntity.ok(Map.of("totalCount", count));
    }

    // UPDATE endpoints
    @PutMapping("/{id}")
    // @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<ActivityResponseDTO> updateActivity(
            @PathVariable Long id,
            @Valid @RequestBody ActivityUpdateDTO updateDTO) {

        ActivityResponseDTO updatedActivity = activityService.updateActivity(id, updateDTO);
        return ResponseEntity.ok(updatedActivity);
    }

    // DELETE endpoints (admin only)
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        Map<String, Object> response = Map.of(
                "message", "Activity deleted successfully",
                "id", id,
                "success", true
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/task/{taskId}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteActivitiesByTask(@PathVariable Long taskId) {
        activityService.deleteActivitiesByTask(taskId);
        Map<String, Object> response = Map.of(
                "message", "Activities deleted for task",
                "taskId", taskId,
                "success", true
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{userId}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteActivitiesByUser(@PathVariable Long userId) {
        activityService.deleteActivitiesByUser(userId);
        Map<String, Object> response = Map.of(
                "message", "Activities deleted for user",
                "userId", userId,
                "success", true
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/cleanup")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> cleanupOldActivities(
            @RequestParam(defaultValue = "90") int daysToKeep) {

        activityService.deleteOldActivities(daysToKeep);
        Map<String, String> response = Map.of(
                "message", "Cleaned up activities older than " + daysToKeep + " days"
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/all")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteAllActivities() {
        activityService.deleteAllActivities();
        Map<String, String> response = Map.of(
                "message", "All activities deleted successfully"
        );
        return ResponseEntity.ok(response);
    }

    // Health check
    @GetMapping("/health")
    // @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getHealthStatus() {
        List<ActivitySummaryDTO> recentActivities = activityService.getRecentActivities(5);
        Map<String, Object> health = Map.of(
                "status", "UP",
                "totalActivities", activityService.getTotalActivityCount(),
                "recentActivities", recentActivities
        );
        return ResponseEntity.ok(health);
    }
}

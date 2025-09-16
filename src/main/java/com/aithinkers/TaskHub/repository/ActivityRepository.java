package com.aithinkers.TaskHub.repository;

import com.aithinkers.TaskHub.entity.Activity;
import com.aithinkers.TaskHub.enums.ActionType;
import com.aithinkers.TaskHub.enums.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Basic queries by single field
    List<Activity> findByTaskId(Long taskId);
    List<Activity> findByPerformedBy(Long userId);
    List<Activity> findByActionType(ActionType actionType);
    List<Activity> findByPriority(Priority priority);

    // With pagination
    Page<Activity> findByTaskId(Long taskId, Pageable pageable);
    Page<Activity> findByPerformedBy(Long userId, Pageable pageable);

    // Ordered queries
    List<Activity> findByTaskIdOrderByTimestampDesc(Long taskId);
    List<Activity> findByPerformedByOrderByTimestampDesc(Long userId);

    // Recent activities
    List<Activity> findTop10ByOrderByTimestampDesc();

    // Date range queries
    List<Activity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Latest activity for a task or user
    Optional<Activity> findTopByTaskIdOrderByTimestampDesc(Long taskId);
    Optional<Activity> findTopByPerformedByOrderByTimestampDesc(Long userId);

    // Count queries
    long countByTaskId(Long taskId);
    long countByPerformedBy(Long userId);

    // Search in action details
    List<Activity> findByActionDetailsContaining(String keyword);

    // Custom query for recent activities with time limit
    @Query("SELECT a FROM Activity a WHERE a.taskId = :taskId AND a.timestamp >= :since ORDER BY a.timestamp DESC")
    List<Activity> findRecentActivitiesForTask(@Param("taskId") Long taskId, @Param("since") LocalDateTime since);

    @Query("SELECT a FROM Activity a WHERE a.performedBy = :userId AND a.timestamp >= :since ORDER BY a.timestamp DESC")
    List<Activity> findRecentActivitiesForUser(@Param("userId") Long userId, @Param("since") LocalDateTime since);

    // Delete operations
    void deleteByTaskId(Long taskId);
    void deleteByPerformedBy(Long userId);
    void deleteByTimestampBefore(LocalDateTime cutoffDate);
}
package com.aithinkers.TaskHub.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_id", nullable = false)
    private Long taskId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Column(name = "action_details", length = 500)
    private String actionDetails;

    @Column(name = "performed_by", nullable = false)
    private Long performedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum ActionType {
        CREATED, UPDATED, COMPLETED, DELETED, ASSIGNED, COMMENTED
    }

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}
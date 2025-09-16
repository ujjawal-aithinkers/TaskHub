package com.aithinkers.TaskHub.repository;

import com.aithinkers.TaskHub.entity.Activity;
import com.aithinkers.TaskHub.enums.ActionType;
import com.aithinkers.TaskHub.enums.Priority;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class ActivityRepositoryTest {
    private final ActivityRepository repo;

    ActivityRepositoryTest(ActivityRepository repo){
        this.repo=repo;
    }

    @Test
    void saveAndFindByTaskId_orderedByTimestampDesc(){
        Long taskid=100L;

        repo.save(Activity.builder()
                .taskId(taskid)
                .actionType(ActionType.CREATED)
                .actionDetails("old")
                .performedBy(1L)
                .priority(Priority.MEDIUM)
                .timestamp(LocalDateTime.now().minusMinutes(10))
                .build());


        repo.save(Activity.builder()
                .taskId(taskid)
                .actionType(ActionType.UPDATED)
                .actionDetails("new")
                .performedBy(1L)
                .priority(Priority.MEDIUM)
                .timestamp(LocalDateTime.now())
                .build());

        List<Activity> list = repo.findByTaskIdOrderByTimestampDesc(taskid);

        assertThat(list).hasSize(2);
        assertThat(list.get(0).getActionType()).isEqualTo(ActionType.UPDATED);
        assertThat(list.get(1).getActionType()).isEqualTo(ActionType.CREATED);
    }
    @Test
    void findRecentActivitiesForTask_filtersBySince() {
        Long taskId = 200L;

        repo.save(Activity.builder()
                .taskId(taskId)
                .actionType(ActionType.CREATED)
                .actionDetails("5h ago")
                .performedBy(1L)
                .priority(Priority.LOW)
                .timestamp(LocalDateTime.now().minusHours(5))
                .build());

        repo.save(Activity.builder()
                .taskId(taskId)
                .actionType(ActionType.UPDATED)
                .actionDetails("1h ago")
                .performedBy(1L)
                .priority(Priority.HIGH)
                .timestamp(LocalDateTime.now().minusHours(1))
                .build());

        List<Activity> list = repo.findRecentActivitiesForTask(taskId, LocalDateTime.now().minusHours(2));

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getActionType()).isEqualTo(ActionType.UPDATED);
    }

    @Test
    void deleteByTimestampBefore_removesOldOnly() {
        repo.save(Activity.builder()
                .taskId(1L)
                .actionType(ActionType.CREATED)
                .actionDetails("old")
                .performedBy(1L)
                .priority(Priority.LOW)
                .timestamp(LocalDateTime.now().minusDays(5))
                .build());

        repo.save(Activity.builder()
                .taskId(1L)
                .actionType(ActionType.CREATED)
                .actionDetails("new")
                .performedBy(1L)
                .priority(Priority.LOW)
                .timestamp(LocalDateTime.now())
                .build());

        repo.deleteByTimestampBefore(LocalDateTime.now().minusDays(1));

        assertThat(repo.count()).isEqualTo(1);
    }
}

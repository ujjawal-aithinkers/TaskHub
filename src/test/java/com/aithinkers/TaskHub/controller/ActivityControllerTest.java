package com.aithinkers.TaskHub.controller;

import com.aithinkers.TaskHub.Controller.ActivityController;
import com.aithinkers.TaskHub.Service.ActivityService;
import com.aithinkers.TaskHub.dto.ActivityCreateDTO;
import com.aithinkers.TaskHub.dto.ActivityResponseDTO;
import com.aithinkers.TaskHub.dto.ActivityUpdateDTO;
import com.aithinkers.TaskHub.enums.ActionType;
import com.aithinkers.TaskHub.enums.Priority;
import com.aithinkers.TaskHub.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ActivityController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ActivityControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityService activityService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createActivity_returns201AndBody() throws Exception {
        ActivityResponseDTO response = ActivityResponseDTO.builder()
                .id(10L).taskId(1L)
                .actionType(ActionType.CREATED)
                .actionDetails("Task created: T1")
                .performedBy(7L).priority(Priority.MEDIUM)
                .timestamp(LocalDateTime.now())
                .build();

        Mockito.when(activityService.createActivity(any(ActivityCreateDTO.class)))
                .thenReturn(response);

        ActivityCreateDTO request = ActivityCreateDTO.builder()
                .taskId(1L).actionType(ActionType.CREATED)
                .actionDetails("Task created: T1")
                .performedBy(7L).priority(Priority.MEDIUM)
                .build();

        mockMvc.perform(post("/api/activities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.taskId").value(1L))
                .andExpect(jsonPath("$.actionDetails").value("Task created: T1"));
    }

    @Test
    void getActivityById_returns200_whenFound() throws Exception {
        ActivityResponseDTO response = ActivityResponseDTO.builder()
                .id(5L).taskId(99L)
                .actionType(ActionType.UPDATED)
                .actionDetails("updated details")
                .performedBy(1L).priority(Priority.HIGH)
                .timestamp(LocalDateTime.now())
                .build();

        Mockito.when(activityService.getActivityById(5L)).thenReturn(response);

        mockMvc.perform(get("/api/activities/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.taskId").value(99L))
                .andExpect(jsonPath("$.actionDetails").value("updated details"));
    }

    @Test
    void getActivityById_returns404_whenNotFound() throws Exception {
        Mockito.when(activityService.getActivityById(42L))
                .thenThrow(new ResourceNotFoundException("Activity not found with id: 42"));

        mockMvc.perform(get("/api/activities/42"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateActivity_returns200_whenFound() throws Exception {
        ActivityResponseDTO response = ActivityResponseDTO.builder()
                .id(7L).taskId(100L)
                .actionType(ActionType.UPDATED)
                .actionDetails("new info")
                .performedBy(2L).priority(Priority.HIGH)
                .timestamp(LocalDateTime.now())
                .build();

        Mockito.when(activityService.updateActivity(eq(7L), any(ActivityUpdateDTO.class)))
                .thenReturn(response);

        ActivityUpdateDTO updateRequest = ActivityUpdateDTO.builder()
                .actionType(ActionType.UPDATED)
                .actionDetails("new info")
                .priority(Priority.HIGH)
                .build();

        mockMvc.perform(put("/api/activities/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L))
                .andExpect(jsonPath("$.actionDetails").value("new info"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    void updateActivity_returns404_whenNotFound() throws Exception {
        Mockito.when(activityService.updateActivity(eq(99L), any(ActivityUpdateDTO.class)))
                .thenThrow(new ResourceNotFoundException("Activity not found with id: 99"));

        ActivityUpdateDTO updateRequest = ActivityUpdateDTO.builder()
                .actionType(ActionType.UPDATED)
                .actionDetails("does not exist")
                .priority(Priority.MEDIUM)
                .build();

        mockMvc.perform(put("/api/activities/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteActivity_returns200_whenDeleted() throws Exception {
        Mockito.doNothing().when(activityService).deleteActivity(5L);

        mockMvc.perform(delete("/api/activities/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Activity deleted successfully"))
                .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    void deleteActivity_returns404_whenNotFound() throws Exception {
        Mockito.doThrow(new ResourceNotFoundException("Activity not found"))
                .when(activityService).deleteActivity(99L);

        mockMvc.perform(delete("/api/activities/99"))
                .andExpect(status().isNotFound());
    }
}

package com.knguyendev.api.mappers.impl;

import com.knguyendev.api.domain.dto.Task.TaskDTO;
import com.knguyendev.api.domain.entities.TaskEntity;
import com.knguyendev.api.mappers.TaskMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskMapperImpl implements TaskMapper {
    @Override
    public TaskDTO toDTO(TaskEntity entity) {
        return TaskDTO.builder()
                // Referential keys
                .id(entity.getId())
                .taskListId(entity.getTaskListId())
                .parentTaskId(entity.getParentTaskId())
                .prevTaskId(entity.getPrevTaskId())
                .nextTaskId(entity.getNextTaskId())
                // Task contents
                .title(entity.getTitle())
                .description(entity.getDescription())
                .isComplete(entity.isComplete())
                .isStarred(entity.isStarred())
                .isAllDay(entity.isAllDay())
                .dueDate(entity.getDueDate())
                .build();
    }
}

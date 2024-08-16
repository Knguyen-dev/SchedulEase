package com.knguyendev.api.mappers.impl;

import com.knguyendev.api.domain.dto.TaskList.TaskListDTO;
import com.knguyendev.api.domain.entities.TaskListEntity;
import com.knguyendev.api.mappers.TaskListMapper;
import org.springframework.stereotype.Component;

@Component
public class TaskListMapperImpl implements TaskListMapper {
    @Override
    public TaskListDTO toDTO(TaskListEntity entity) {
        return TaskListDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .isDefault(entity.isDefault())
                .build();
    }
}

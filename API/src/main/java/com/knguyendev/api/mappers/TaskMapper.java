package com.knguyendev.api.mappers;

import com.knguyendev.api.domain.dto.Task.TaskDTO;
import com.knguyendev.api.domain.entities.TaskEntity;

public interface TaskMapper {
    TaskDTO toDTO(TaskEntity entity);
}

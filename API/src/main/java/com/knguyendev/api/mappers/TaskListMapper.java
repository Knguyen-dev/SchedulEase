package com.knguyendev.api.mappers;

import com.knguyendev.api.domain.dto.TaskList.TaskListDTO;
import com.knguyendev.api.domain.entities.TaskListEntity;

public interface TaskListMapper {

    /**
     * Maps a TaskListEntity into a TaskListDTO that's used for sending
     * back to the client
     * @param entity TaskListEntity that's being mapped.
     * @return The response dto used to send back information about a given TaskList.
     */
    TaskListDTO toDTO(TaskListEntity entity);
}

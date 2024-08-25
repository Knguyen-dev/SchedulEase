package com.knguyendev.api.domain.dto.TaskList;

import com.knguyendev.api.domain.dto.Task.TaskDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskListDTO {
    private Long id;
    private Long userId;
    private String name;
    private List<TaskDTO> tasks;
    private boolean isDefault;
}

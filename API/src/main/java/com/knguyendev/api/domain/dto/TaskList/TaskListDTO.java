package com.knguyendev.api.domain.dto.TaskList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskListDTO {
    private Long id;
    private Long userId;
    private String name;
    private boolean isDefault;
}

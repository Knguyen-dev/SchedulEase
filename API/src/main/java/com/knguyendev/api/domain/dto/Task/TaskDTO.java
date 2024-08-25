package com.knguyendev.api.domain.dto.Task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

/**
 * DTO for sending back a task to the client.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskDTO {
    /*
     * This DTO is designed to focus on the essential details of a task without including
     * related entities such as TaskList, parentTask, etc. By only including the task's
     * own attributes and IDs for related entities, we simplify the response and avoid
     * exposing unnecessary details.
     *
     * If future requirements necessitate sending more comprehensive information about
     * related entities, we can adapt the DTO to include those details as needed.
     */
    private Long id;
    private Long taskListId;
    private Long parentTaskId;
    private Long prevTaskId;
    private Long nextTaskId;

    // Task content info
    private String title;
    private String description;
    private boolean isComplete;
    private boolean isStarred;
    private boolean isAllDay;
    private ZonedDateTime dueDate;
}

package com.knguyendev.api.domain.dto.Task;

import com.knguyendev.api.domain.dto.Task.constraints.DescriptionConstraint;
import com.knguyendev.api.domain.dto.Task.constraints.TitleConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

/**
 * DTO for updating the content of a Task
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskUpdateRequest {
    /**
     * The title of the task.
     * This field is required and validated by the TitleConstraint to ensure it meets length and character requirements.
     */
    @TitleConstraint
    private String title;

    /**
     * Description of the task.
     * This field is optional. If provided, it is validated by the DescriptionConstraint to ensure it does not exceed 300 characters.
     */
    @DescriptionConstraint
    private String description;

    /**
     * Whether the task is marked as complete or not
     */
    private boolean isComplete;

    /**
     * Indicates whether the task is tied to a specific day rather than a specific time. When `isAllDay` is true, it means
     * the task is not restricted to a specific time of day and should be treated as spanning the entire day. This could imply:
     * - The task is due at the end of the specified day, making it effectively due by the end of the day in the user's local time.
     * - The task is something the user needs to complete or work on throughout the entire day.
     * The exact interpretation of `isAllDay` may vary depending on the user's context and preference.
     */
    private boolean isAllDay;

    /**
     * Indicates whether the task is starred or marked as important.
     * This field is optional and defaults to false if not specified.
     */
    private boolean isStarred;

    /**
     * Due date of the task in UTC time.
     * This field is optional. If provided, it indicates the deadline for the task. The frontend of the application should
     * render the dueDate in the user's local time. As well as this, if isAllDay is true, then this shouldn't have a
     * time portion. To handle this, I think you'd have to set the timestamp to midnight?
     * <p>
     * If `isAllDay` is true, the `dueDate` should not have a time portion, since the task is associated with a day rather
     * than a specific time. To handle this:
     * - The time portion should be set to midnight (00:00:00) in UTC time.
     *
     */
    private ZonedDateTime dueDate;

    public void normalizeData() {
        title = title.trim();
        description = description.trim();
    }
}

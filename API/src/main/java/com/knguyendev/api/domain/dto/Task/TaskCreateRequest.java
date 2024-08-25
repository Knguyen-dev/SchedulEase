package com.knguyendev.api.domain.dto.Task;

import com.knguyendev.api.domain.dto.Task.constraints.DescriptionConstraint;
import com.knguyendev.api.domain.dto.Task.constraints.TitleConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * DTO for creating a new task or updating an existing task. This DTO model should be able to cover a variety of use
 * cases:
 * <p>
 *   1. **Create a Task at the Top of the List:** Set `prevTaskId` to `null` and `isSubTask` to `false`. The task will be placed at the top of the specified `taskListId`. Note that
 *   setting isSubTask to false isn't entirely necessary, because if prevTaskId is null then obviously there's no parentTask.
 *   It's just the principle or idea.
 * <p>
 *   2. **Create a Subtask:** Set `prevTaskId` to the ID of the parent task and `isSubTask` to `true`. This will place the new task as a subtask of the specified parent task.
 * <p>
 *   3. **Insert a Task Below an Existing Task:** Provide `prevTaskId` with the ID of the task that should precede the new task, and set `isSubTask` to `false`. The new task will be placed immediately after the specified task within the same list.
 * </p>
 * Let's say you then wanted to update the title of a task, something simple that doesn't involve any relationships.
 * You would just need to pass in the exact same referential information such as prevTaskId, isSubTask, and taskListId.
 * The service function would then check these values, if they are all the same, then the service function wouldn't make
 * any changes to any relationships. Then you can get to the logic for updating the task's contents.
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskCreateRequest {
    /**
     * ID of the task list to which this task belongs.
     * This field is required because every task must be associated with a task list.
     */
    private Long taskListId;

    /**
     * ID of the previous task that this task should be placed behind.
     * This field is optional. If provided, it indicates that the new task should be placed after the task with this ID.
     * If not provided, the new task will be placed at the top of the list.
     * <p>
     * NOTE: If `prevTaskId` is provided, its `taskListId` should match the `taskListId` of this task.
     * If there is a mismatch, the application should prioritize `prevTaskId`'s task list or
     * maybe throw an error to say something wrong.
     * </p>
     */
    private Long prevTaskId;

    // Indicates whether the current task being created is going to be a subtask of prevTask; however remember that
    // this doesn't exclude the task being created from being a subtask in a list of subtasks!
    private boolean isSubTask;

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
     * Whether the task being created is marked as complete or not
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

package com.knguyendev.api.domain.dto.Task;

/**
 * DTO used to specifically update an existing task's ordering within its task list or across different task lists.
 * <p>
 * NOTE 1: The `parentTaskId` is not included in this DTO because reordering tasks does not directly involve changing
 * a task's parent-child relationship. When moving a task, it might become a child task of another task if it's
 * moved between a parent task and its last subtask. However, we do not explicitly support reordering tasks to
 * directly modify their parent-child relationships. This aspect will be handled separately in the service logic.
 * <p>
 * NOTE 2:
 * + 3 scenarios:
 * 1. prevTask: Moving currentTask after an existing prev task, regardless of where the existing prevTask is. Whether it's
 *    in another taskList or if it's in the currentTaskList
 * 2. !prevTaskId and then taskListId: Moving currentTask to the top of a different taskList. This is because prevTask isn't given
 *    so we know in this new position the task has no previous ones!
 * 3. !prevTaskId and then !taskListId defined: Moving currentTask to the top of its own list. No prevTask so it's not
 */
public class TaskOrderRequest {

    // ID of the task list that we are moving the current task into
    private Long taskListId;

    // ID of the task that we are inserting the current task after. This is optional and can be null
    private Long prevTaskId;
}
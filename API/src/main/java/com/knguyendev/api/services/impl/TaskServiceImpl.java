package com.knguyendev.api.services.impl;

import com.knguyendev.api.domain.dto.Task.TaskDTO;
import com.knguyendev.api.domain.dto.Task.TaskCreateRequest;
import com.knguyendev.api.domain.dto.Task.TaskUpdateRequest;
import com.knguyendev.api.domain.entities.TaskEntity;
import com.knguyendev.api.exception.ServiceException;
import com.knguyendev.api.mappers.TaskMapper;
import com.knguyendev.api.repositories.TaskListRepository;
import com.knguyendev.api.repositories.TaskRepository;
import com.knguyendev.api.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TaskServiceImpl implements TaskService {
    private final TaskListRepository taskListRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    public TaskServiceImpl(TaskListRepository taskListRepository, TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskListRepository = taskListRepository;
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    private TaskEntity getTaskEntity(Long id) {
        Optional<TaskEntity> result = taskRepository.findById(id);
        if (result.isEmpty()) {
            throw new ServiceException("Task with id '" + id + "' wasn't found!", HttpStatus.NOT_FOUND);
        }
        return result.get();
    }

    /**
     * Filters out null tasks and removes duplicates by their ID, ensuring only unique tasks are included.
     *
     * @param tasks A list of TaskEntity objects to be filtered.
     * @return A list of unique, non-null TaskEntity objects.
     */
    private List<TaskEntity> filterUniqueNonNullTasks(List<TaskEntity> tasks) {
        Set<Long> uniqueTaskIds = new HashSet<>();
        return tasks.stream()
                .filter(Objects::nonNull) // removes null tasks
                .filter(task -> uniqueTaskIds.add(task.getId())) // adds to set, if it wasn't already in set, then true, else false
                .toList();
    }

    /**
     * Given a complete list of subtasks, get the subTask at the tail of the list
     * @param subTasks Assumed to be a complete list of subTasks
     * @return TaskEntity that represents the last subtask in the list of subtasks.
     */
    public TaskEntity getLastSubTask(List<TaskEntity> subTasks) {
        // Step 1: Create a set of subtask IDs
        Set<Long> subTaskIds = subTasks.stream()
                .map(TaskEntity::getId)
                .collect(Collectors.toSet());
        // Step 2: Iterate through the subtasks
        for (TaskEntity subTask : subTasks) {
            Long nextTaskId = subTask.getNextTaskId();
            // Step 3: Check if the nextTask is in the set of subtask IDs
            if (nextTaskId == null || !subTaskIds.contains(nextTaskId)) {
                // If not, then this subTask is the last subTask
                return subTask;
            }
        }
        // If all subtasks point to another subtask, return null or throw an exception
        throw new ServiceException("No last subTask found!", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Helper function for deleting a task alongside its subtasks
     * @param targetTask The parent task, which is the task that's being deleted in question
     * @param subTasks The list of subtasks for the parent task. These are tasks that reference
     *                 the parentTask in the TaskEntity.parentTask attribute.
     * <p>
     * NOTE: The prevTask would be the task before the parentTask, and then the nextTask is the task after the
     * parentTask's very last subtask. The prevTask and nextTask could both be null. This can happen when the only tasks in the task list are
     * that parent task and its subtasks. So deleting the parentTask and its subtasks would make the task list empty.
     */
    private void deleteParentTask(TaskEntity targetTask, List<TaskEntity> subTasks) {
        TaskEntity lastSubTask = getLastSubTask(subTasks);
        List<Long> taskIds = Stream.of(
                        targetTask.getPrevTaskId(),
                        lastSubTask.getNextTaskId()
                )
                .filter(Objects::nonNull) // Ensure no null IDs are included
                .toList();
        List<TaskEntity> relatedTasks = taskRepository.findAllByIds(taskIds);
        TaskEntity prevTask = relatedTasks.stream()
                .filter(t -> t.getId().equals(targetTask.getPrevTaskId()))
                .findFirst()
                .orElse(null);
        TaskEntity nextTask = relatedTasks.stream()
                .filter(t -> t.getId().equals(lastSubTask.getNextTaskId()))
                .findFirst()
                .orElse(null);

        /*
         * + Link prevTask and nextTask, if they exist:
         *   - If both prevTask and nextTask are defined, set the nextTask pointer of prevTask
         *     to nextTask, and the prevTask pointer of nextTask to prevTask.
         *   - If only prevTask is defined, set its nextTask pointer to null,
         *     indicating that it's now the tail of the task list.
         *   - If only nextTask is defined, set its prevTask pointer to null,
         *     indicating that it's now the head of the task list.
         */
        if (prevTask != null && nextTask != null) {
            prevTask.setNextTaskId(nextTask.getId());
            nextTask.setPrevTaskId(prevTask.getId());
        } else if (prevTask != null) {
            prevTask.setNextTaskId(null);
        } else if (nextTask != null) {
            nextTask.setPrevTaskId(null);
        }

        // From prevTask and nextTask, get only the non-null ones; Save all tasks
        // NOTE: Generally doing .saveAll() is more efficient than two separate .save() functions
        List<TaskEntity> tasksToSave = filterUniqueNonNullTasks(Arrays.asList(prevTask, nextTask));
        taskRepository.saveAll(tasksToSave);

        // Delete the parent task and all subtasks
        taskRepository.deleteTaskAndSubTasksById(targetTask.getId());
    }

    /**
     * For deleting a task that isn't a parentTask.
     * @param targetTask Task that isn't a parentTask, meaning that its id isn't reference as parentTaskId to any other task
     */
    private void deleteNonParentTask(TaskEntity targetTask) {
        // Collect the IDs for prevTask and nextTask, find them
        List<Long> taskIds = Stream.of(
                        targetTask.getPrevTaskId(),
                        targetTask.getNextTaskId()
                )
                .filter(Objects::nonNull) // Ensure no null IDs are included
                .toList();
        List<TaskEntity> relatedTasks = taskRepository.findAllByIds(taskIds);
        TaskEntity prevTask = relatedTasks.stream()
                .filter(task -> task.getId().equals(targetTask.getPrevTaskId()))
                .findFirst()
                .orElse(null);
        TaskEntity nextTask = relatedTasks.stream()
                .filter(task -> task.getId().equals(targetTask.getNextTaskId()))
                .findFirst()
                .orElse(null);
        /*
         * + Link prevTask and nextTask, if they exist:
         *   - If both prevTask and nextTask are defined, set the nextTask pointer of prevTask
         *     to nextTask, and the prevTask pointer of nextTask to prevTask.
         *   - If only prevTask is defined, set its nextTask pointer to null,
         *     indicating that it's now the tail of the task list.
         *   - If only nextTask is defined, set its prevTask pointer to null,
         *     indicating that it's now the head of the task list.
         */
        if (prevTask != null && nextTask != null) {
            prevTask.setNextTaskId(nextTask.getId());
            nextTask.setPrevTaskId(prevTask.getId());
        } else if (prevTask != null) {
            prevTask.setNextTaskId(null);
        } else if (nextTask != null) {
            nextTask.setPrevTaskId(null);
        }

        // From prevTask and nextTask, get only the non-null ones, then save all tasks
        // NOTE: Generally this is more efficient than doing two .save() calls
        List<TaskEntity> tasksToSave = filterUniqueNonNullTasks(Arrays.asList(prevTask, nextTask));
        taskRepository.saveAll(tasksToSave);

        // Now at all the pointer logic is settled, deleted the target task
        taskRepository.deleteById(targetTask.getId());
    }

    /**
     * Creates a task at the top of a given task list. In this case we ignore the
     * prevTaskId and isSubTask properties, and rely on taskListId.
     * @param taskRequest Object that contains the information needed to create a task
     */
    private TaskEntity createTaskAtTopOfTaskList(TaskCreateRequest taskRequest) {
        // Check that the task list exists and throw an error when it doesn't
        taskListRepository.findById(taskRequest.getTaskListId())
                .orElseThrow(() -> new ServiceException("Task list with id '"
                        + taskRequest.getTaskListId() + "' wasn't found!", HttpStatus.NOT_FOUND));
        // Build the new task entity
        TaskEntity newTask = TaskEntity.builder()
                .taskListId(taskRequest.getTaskListId())
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .isComplete(taskRequest.isComplete())
                .isAllDay(taskRequest.isAllDay())
                .isStarred(taskRequest.isStarred())
                .dueDate(taskRequest.getDueDate())
                .build();
        /*
         * Link the new task to the current head of the task list, if it exists:
         * - If a head task exists, link it as the next task of the new task.
         *   Then, save the new task to generate its ID and update the head task's
         *   prevTask reference to the new task.
         * - Else, no head task exists (i.e., the list is empty), simply save the new task.
         */
        Optional<TaskEntity> existingTaskResult = taskRepository.findTaskListHeadByTaskListId(taskRequest.getTaskListId());
        if (existingTaskResult.isPresent()) {
            TaskEntity existingTask = existingTaskResult.get();
            newTask.setNextTaskId(existingTask.getId());
            newTask = taskRepository.save(newTask);
            existingTask.setPrevTaskId(newTask.getId());
            taskRepository.save(existingTask);
        } else {
            newTask = taskRepository.save(newTask);
        }
        return newTask;
    }

    /**
     * Creates a new task as a subtask of a parentTask
     * @param parentTask The task that is assumed to be a parent task that is guaranteed to exist
     * @param prevTask The task that goes before the newly created task. At least, you'll know that prevTask will exist because
     *                 it will be the parentTask or another subtask of the parentTask.
     * @param nextTask The next that goes after the newly created task. This can be null as there's the chance the task being
     *                 created will become the tail of the task list that the parentTask is in.
     * @param taskCreateRequest The request object that has the necessary information to create a task. Here we won't be using taskListId or isSubTask, as the former
     *                    can be garnered from parentTask, prevTask, and nextTask since all of those tasks are assumed to be in the same list. The latter
     *                    isn't used because it's already known that the newly created task is going to be a subTask.
     * @return The TaskEntity representing the newly created ask
     * <p>
     * NOTE: It should be noted that parentTask and prevTask may sometimes refer to the same task. Especially in the task where you're creating a new
     * task right below an existing parentTask. In this case the newly created task's prevTask will be that parentTask.
     */
    private TaskEntity createTaskAsSubTask(TaskEntity parentTask, TaskEntity prevTask, TaskEntity nextTask, TaskCreateRequest taskCreateRequest) {

        Long nextTaskId = nextTask != null ? nextTask.getId() : null;
        // Build the new task entity and save it into the database we get its ID value, after we'll update pointers
        TaskEntity newTask = TaskEntity.builder()
                .taskListId(parentTask.getTaskListId())
                .parentTaskId(parentTask.getId())
                .prevTaskId(prevTask.getId())
                .nextTaskId(nextTaskId)
                .title(taskCreateRequest.getTitle())
                .description(taskCreateRequest.getDescription())
                .isComplete(taskCreateRequest.isComplete())
                .isAllDay(taskCreateRequest.isAllDay())
                .isStarred(taskCreateRequest.isStarred())
                .dueDate(taskCreateRequest.getDueDate())
                .build();
        newTask = taskRepository.save(newTask);

        // Now we need to update the prevTask and nextTask's pointers so that they point to the newly created task
        prevTask.setNextTaskId(newTask.getId());
        if (nextTask != null) {
            nextTask.setPrevTaskId(newTask.getId());
        }

        List<TaskEntity> tasksToSave = filterUniqueNonNullTasks(Arrays.asList(prevTask, nextTask));
        taskRepository.saveAll(tasksToSave);
        return newTask;
    }

    // NOTE: This is not finished, so we'll need to test it.
//    /**
//     * Function that's used to update the ordering of an existing task.
//     * @param targetTask The task being re-ordered
//     * @param prevTaskId The ID of the new task that's supposed to come before the targetTask. This could be null since it's an
//     *                   optional parameter, in which case this function will rely on 'taskListId' for reordering. However,
//     *                   if prevTaskId is defined, then we'll ignore 'taskListId'
//     * @param taskListId ID of the taskList that we're moving the targetTask to. This is only used when prevTaskId is null, so
//     *                   in this scenario we're moving the targetTask to the top of another taskList.
//     * @return Entity representing the re-ordered and updated task, applied with changes, if any.
//     */
//    private TaskEntity reorderTask(TaskEntity targetTask, List<TaskEntity> subTasks, Long prevTaskId, Long taskListId, MutableBoolean isDiff) throws ServiceException {
//
//
//        // If both are missing, throw an error;
//        if (prevTaskId == null && taskListId == null) {
//            throw new ServiceException("'prevTaskId' and 'taskListId' can't both be missing. If you aren't passing 'prevTaskId', you must pass taskListId!", HttpStatus.BAD_REQUEST);
//        }
//
//        /*
//         * Validates that the targetTask is not set as its own previous task.
//         * A task cannot reference itself as its previous task, as this would create an invalid relationship.
//         */
//        if (targetTask.getId().equals(prevTaskId)) {
//            throw new ServiceException("Task's id and the prevTaskId '" + prevTaskId + "' are equal. Please don't!", HttpStatus.BAD_REQUEST);
//        }
//
//
//        // Determine if targetTask is a parentTask
//        boolean isParentTask = !subTasks.isEmpty();
//
//        // If the id of the prevTask and the task list hasn't changed, then the order of the targetTask should not have changed
//        if (Objects.equals(prevTaskId, targetTask.getPrevTask().getId()) && Objects.equals(taskListId, targetTask.getTaskListId())) {
//            return targetTask;
//        }
//
//        // If prevTaskId is null, we're moving the targetTask to a new task list
//        if (prevTaskId == null) {
//            if (targetTask.getTaskListId().equals(taskListId)) {
//                // Case 1: Moving a task to the top of the same task list
//                TaskEntity prevTask = targetTask.getPrevTask();
//
//                // Subcase: if there's no prevTask, then targetTask is already at the top of its own task list.
//                if (prevTask == null) {
//                    return targetTask;
//                }
//
//                if (isParentTask) {
//                    /*
//                     * Subcase: parentTask is being moved to the top of its own taskList. The parentTask and its subtasks should be moved.
//                     *
//                     * 1. update pointers on prevTask and nextTask so that they don't point to the targetTask or the last
//                     * subtask; nextTask may be null which happens when the last subtask of our
//                     * parent task is at the tail of our task list.
//                     *
//                     * 2. Update pointers on the targetTask so that it's prevTask is null, since it's going to be the new head.
//                     * Then update lastSubTask to point at the current head.
//                     *
//                     * 3. Save tasks to the database? I think things should be good.
//                     */
//                    TaskEntity lastSubTask = getLastSubTask(subTasks);
//                    TaskEntity nextTask = lastSubTask.getNextTask();
//                    targetTask.setPrevTask(null);
//                    prevTask.setNextTask(nextTask);
//                    if (nextTask != null) {
//                        nextTask.setPrevTask(prevTask);
//                    }
//                    Optional<TaskEntity> currentHeadResult = taskRepository.findTaskListHeadByTaskListId(taskListId);
//                    if (currentHeadResult.isEmpty()) {
//                        throw new ServiceException("Reference error, the head of the task list wasn't found but we expected it!", HttpStatus.INTERNAL_SERVER_ERROR);
//                    }
//                    TaskEntity currentHead = currentHeadResult.get();
//                    lastSubTask.setNextTask(currentHead);
//                    currentHead.setPrevTask(lastSubTask);
//
//                    List<TaskEntity> combinedTasks = new ArrayList<>();
//                    combinedTasks.add(targetTask); // Task being moved
//                    combinedTasks.add(prevTask); // Task before the targetTask
//                    combinedTasks.add(nextTask); // Task after the lastSubTask
//                    combinedTasks.add(lastSubTask); // the last subtask which points to the current head of the task list
//                    combinedTasks.add(currentHead); // the current head of the task list that gets a previous task; and as a result becoming the former head
//
//                    // Save changes into the database.
//                    taskRepository.saveAll(filterNonNullTasks(combinedTasks));
//                    return targetTask;
//                }
//
//                if (targetTask.getParentTask() != null) {
//                    /*
//                     * Subcase: The targetTask isn't a parentTask, but is rather a sub-task of a parent task. In this case, moving it to the top of
//                     * the task list would mean it isn't a subtask anymore.
//                     *
//                     * 1. Update the pointers prevTask and nextTask, the latter may be null, which happens when targetTask
//                     * is at the tail of the task list.
//                     *
//                     * 2. Update the targetTask so that it no longer references a parentTask, and as a result is no longer
//                     * a subtask. As well as this, the targetTask.prevTask should be null to indicate that the targetTask
//                     * has become the head of the task list. Then the targetTask.nextTask should point to the current head
//                     *
//                     */
//                    TaskEntity nextTask = targetTask.getNextTask();
//                    prevTask.setNextTask(nextTask);
//                    if (nextTask != null) {
//                        nextTask.setPrevTask(prevTask);
//                    }
//                    Optional<TaskEntity> currentHeadResult = taskRepository.findTaskListHeadByTaskListId(taskListId);
//                    if (currentHeadResult.isEmpty()) {
//                        throw new ServiceException("Reference error, the head of the task list wasn't found but we expected it!", HttpStatus.INTERNAL_SERVER_ERROR);
//                    }
//                    TaskEntity currentHead = currentHeadResult.get();
//
//                    targetTask.setParentTask(null);
//                    targetTask.setNextTask(currentHead);
//                    currentHead.setPrevTask(targetTask);
//
//                    List<TaskEntity> combinedTasks = new ArrayList<>();
//                    combinedTasks.add(targetTask); // Task being moved
//                    combinedTasks.add(prevTask); // Task before the targetTask
//                    combinedTasks.add(nextTask);
//                    combinedTasks.add(currentHead);
//                    taskRepository.saveAll(filterNonNullTasks(combinedTasks));
//                    return targetTask;
//                }
//
//                /*
//                 * Subcase: The task being re-ordered to the top of the task list is a base task
//                 * 1. Update prevTask and nextTask to point to each other instead of the targetTask, where nextTask could
//                 * be null in the case when targetTask is at the tail of the task list.
//                 * 2. Update targetTask.prevTask to be null since it's going to be at teh top of the task list, and
//                 * make sure that its .nextTask() property points to the current head of the task list.
//                 * 3. Update the head of the current task list such that its .prevTask() points to the targetTask.
//                 * 4. Apply changes to the database and return the updated targetTask
//                 */
//                TaskEntity nextTask = targetTask.getNextTask();
//                prevTask.setNextTask(nextTask);
//                if (nextTask != null) {
//                    nextTask.setPrevTask(prevTask);
//                }
//                Optional<TaskEntity> currentHeadResult = taskRepository.findTaskListHeadByTaskListId(taskListId);
//                if (currentHeadResult.isEmpty()) {
//                    throw new ServiceException("Reference error, the head of the task list wasn't found but we expected it!", HttpStatus.INTERNAL_SERVER_ERROR);
//                }
//                TaskEntity currentHead = currentHeadResult.get();
//                targetTask.setPrevTask(null);
//                targetTask.setNextTask(currentHead);
//                currentHead.setPrevTask(targetTask);
//
//                List<TaskEntity> combinedTasks = new ArrayList<>();
//                combinedTasks.add(prevTask);
//                combinedTasks.add(nextTask);
//                combinedTasks.add(currentHead);
//                combinedTasks.add(targetTask);
//                taskRepository.saveAll(filterNonNullTasks(combinedTasks));
//                return targetTask;
//            } else {
//                // Case 2: Moving a task to the top of a different list
//                System.out.println("Yeh");
//
//            }
//        }
//
//        // Else the case here is that the prevTaskId was passed and it was something different.
//        return null;
//    }

    @Override
    public TaskDTO toggleTaskIndentationById(Long id) throws ServiceException {
        // Do query, and allocate the potential tasks
        List<TaskEntity> tasks = taskRepository.findTaskPrevNext(id);
        TaskEntity targetTask = null;
        TaskEntity prevTask = null;
        TaskEntity nextTask = null; // nextTask may stay null
        for (TaskEntity task : tasks) {
            if (task.getId().equals(id)) {
                targetTask = task;
            } else if (Objects.equals(task.getPrevTaskId(), id)) {
                nextTask = task;
            } else if (Objects.equals(task.getNextTaskId(), id)) {
                prevTask = task;
            }
        }

        // If there was no targetTask, then throw an error
        if (targetTask == null) {
            throw new ServiceException("Task with id '" + id + "' wasn't found!", HttpStatus.NOT_FOUND);
        }

        // targetTask is guaranteed; if parentTaskId is defined, we'll find all subtasks for the target task's parent
        Long parentTaskId = targetTask.getParentTaskId();
        List<TaskEntity> subTasks = parentTaskId != null
                ? taskRepository.findTaskAndSubtasksById(parentTaskId)
                .stream()
                .filter(t -> Objects.equals(t.getParentTaskId(), parentTaskId))
                .toList()
                : Collections.emptyList();

        // Case 1: Handle case where task can't be indented since it's the only task in the list
        if (prevTask == null && nextTask == null) {
            throw new ServiceException("Task can't be indented or unindented since it is the only one in the list!", HttpStatus.BAD_REQUEST);
        }

        // the targetTask is indented when it has a parentTask
        boolean isIndented = targetTask.getParentTaskId() != null;

        if (isIndented) {
            // Case 1: It should be a subtask at this point, so set its parent task to null, apply changes, and
            // However, one more thing is that if this is in the middle of a sublist, then place this at the end of said sublist
            targetTask.setParentTaskId(null);

            // Subcase 1: If our targetTask has a task after it, and that task after it is a subtask. Do the idea of relocating
            // the newly unindented task. Put the targetTask after the last subtask.
            if (nextTask != null && nextTask.getParentTaskId() != null) {

                if (prevTask == null) {
                    throw new ServiceException("Task was indented, but didn't have a previous task. A server-side error!", HttpStatus.INTERNAL_SERVER_ERROR);
                }

                // Connect the tasks that surrounded the targetTask to each other; prevTask should exist, at minimum be the parent
                prevTask.setNextTaskId(nextTask.getId());
                nextTask.setPrevTaskId(prevTask.getId());

                // Get the last subtask and the task after it; the latter may not exist if the last subtask is the tail of the task list.
                TaskEntity lastSubTask = getLastSubTask(subTasks);
                TaskEntity newNextTask = lastSubTask.getNextTaskId() != null
                        ? getTaskEntity(lastSubTask.getNextTaskId())
                        : null;

                // Place the targetTask after the lastSubTask; because when unindenting, we want to place it after the last subtask
                lastSubTask.setNextTaskId(targetTask.getId());
                targetTask.setPrevTaskId(lastSubTask.getId());

                // Place the targetTask after the lastSubTask
                if (newNextTask != null) {
                    targetTask.setNextTaskId(newNextTask.getId());
                    newNextTask.setPrevTaskId(targetTask.getId());
                } else {
                    targetTask.setNextTaskId(null); // Clear nextTaskId if no newNextTask exists
                }

                // Collect all tasks involved and save them, ensuring only unique, non-null tasks are saved
                List<TaskEntity> combinedTasks = new ArrayList<>(List.of(prevTask, nextTask, lastSubTask, targetTask));
                if (newNextTask != null) {
                    combinedTasks.add(newNextTask);
                }

                taskRepository.saveAll(filterUniqueNonNullTasks(combinedTasks));
                return taskMapper.toDTO(targetTask);
            }
        } else {
            /*
             * Case 2: The targetTask is unindented, and we're trying to indent it
             * Subcase 1: If a task is being indented, but it doesn't have a previous task, then it's at the top of its own task list and can't be indented.
             */
            if (prevTask == null) {
                throw new ServiceException("Task that you want to indent is already at the top of its own task list!", HttpStatus.BAD_REQUEST);
            }
            /*
             * Subcase 2: If the previous task is a subtask, then ensure the targetTask now shares the parent of the
             * previous task. This covers the case when you're indenting directly below a list of subtasks.
             */
            if (prevTask.getParentTaskId() != null) {
                targetTask.setParentTaskId(prevTask.getParentTaskId());
                taskRepository.save(targetTask);
                return taskMapper.toDTO(targetTask);
            }
            /*
             * Subcase 3: Previous task to the targetTask is a base task (not a parent and not a child)
             * Set the parentTask of the targetTask to the previous task. As a result, the previous task becomes a
             * parent, and the targetTask becomes a child of the previous
             */
            targetTask.setParentTaskId(prevTask.getId());
        }
        taskRepository.save(targetTask);
        return taskMapper.toDTO(targetTask);
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws ServiceException {
        /*
         * + Algorithm for deleting a task:
         *
         * 1. **Determine if the task is a parent task:**
         *    - If the task is a parent task (i.e., it has child tasks), you need to:
         *      1. Identify the previous task of the targetTask (prevTask) and the lastSubTask's next task (nextTask) in the sequence.
         *      2. Connect the prevTask and nextTask.
         *
         * 2. **Handle references when deleting a parent task with child tasks:**
         *    - If both prevTask and nextTask exist:
         *      1. Connect prevTask to nextTask by setting `prevTask.setNextTask(nextTask)`.
         *      2. Set `nextTask.setPrevTask(prevTask)`.
         *    - If only prevTask exists and nextTask does not:
         *      1. Set `prevTask.setNextTask(null)`, as there is no next task.
         *    - If only nextTask exists and prevTask does not:
         *      1. Set `nextTask.setPrevTask(null)`, as there is no previous task.
         *
         * 3. **Handle references when deleting a non-parent task (a regular task):**
         *    - If the task being deleted is not a parent task:
         *      1. Obtain its references, `prevTask` and `nextTask`.
         *      2. Update `prevTask` to point to `nextTask` by setting `prevTask.setNextTask(nextTask)`.
         *      3. Update `nextTask` to point to `prevTask` by setting `nextTask.setPrevTask(prevTask)`.
         *      4. Handle edge cases where either `prevTask` or `nextTask` might be null:
         *         - If `prevTask` is null, no update needed on `prevTask`.
         *         - If `nextTask` is null, no update needed on `nextTask`.
         *
         * 4. **Finally, delete the target task.**
         */

        // Fetch target task and any subtasks
        List<TaskEntity> tasks = taskRepository.findTaskAndSubtasksById(id);
        // Find the target task (the task with the specific ID)
        TaskEntity targetTask = tasks.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ServiceException("Task with ID '" + id + "' not found!", HttpStatus.NOT_FOUND));
        // Find all subtasks (tasks whose parentTaskId is the same as the target task's ID)
        List<TaskEntity> subTasks = tasks.stream()
                .filter(t -> t.getParentTaskId() != null && t.getParentTaskId().equals(id))
                .toList();

        // If the list of subTasks is empty, then the task being deleted isn't a parent task (a subtask or regular task, in any case, you're deleting one task)
        if (subTasks.isEmpty()) {
            deleteNonParentTask(targetTask);
        } else {
            // There exists a list of subtasks, so the targetTask being deleted is a parent task
            deleteParentTask(targetTask, subTasks);
        }
    }

    @Override
    @Transactional
    public TaskDTO create(TaskCreateRequest taskCreateRequest) throws ServiceException {
        /*
         * Algorithm for creating a task in a task list:
         *
         * 1. If `prevTaskId` is null:
         *    - We are creating a new task at the top of the task list.
         *    - Call `createTaskAtTopOfTaskList(taskRequest)` to insert the task at the top.
         * 2. Retrieve the previous task and any subtasks associated with `prevTaskId`:
         *    - If no task is found, throw a `ServiceException`.
         *    - Extract the previous task and its subtasks from the result.
         * 3. If the previous task has subtasks, then this means the previous task is a parent task:
         *    - Insert the new task as a subtask of the previous task, between the previous task and its next task (if any).
         *    - The new task is saved as a subtask of the previous task.
         * 4. If `taskRequest.isSubTask()` is true, the user wants to create a task as a subtask:
         *    - Ensure the previous task is not already a subtask (a subtask cannot have its own subtasks).
         *    - Insert the new task as a subtask under the previous task.
         * 5. If `nextTask` is not null and the previous task and its next task share the same parent:
         *    - This is the idea that the user is inserting/creating a new task between two existing subtasks.
         *    - In this case, insert the new task as a subtask between the previous task and its next task.
         * 6. Insert the new task after the previous task. Previous task could be a base task or a subtask, but the newly
         *    inserted task will be a base task. If `nextTask` is null, this means the newly created task is the tail task list.
         * 7. Save the new task and update pointers:
         *    - Save the new task to assign it an ID.
         *    - Update the `nextTask` of the previous task to point to the new task.
         *    - If `nextTask` is not null, update its `prevTask` to point to the new task.
         *    - Save the updated tasks to the repository.
         * 7. Return the newly created task as a DTO.
         */

        // If prevTaskId is null, we're creating a new task at the top of the list
        if (taskCreateRequest.getPrevTaskId() == null) {
            TaskEntity newTask = createTaskAtTopOfTaskList(taskCreateRequest);
            return taskMapper.toDTO(newTask);
        }

        // Retrieve the previous task and any subtasks; if none found, throw an exception
        List<TaskEntity> tasks = taskRepository.findTaskAndSubtasksById(taskCreateRequest.getPrevTaskId());
        if (tasks.isEmpty()) {
            throw new ServiceException("Task with id '" + taskCreateRequest.getPrevTaskId() + "' wasn't found!", HttpStatus.NOT_FOUND);
        }
        // Identify the prevTask indicated in the request, and potential subtasks
        TaskEntity prevTask = tasks.stream()
                .filter(t -> t.getId().equals(taskCreateRequest.getPrevTaskId()))
                .findFirst()
                .orElseThrow(() -> new ServiceException("Task with id '" + taskCreateRequest.getPrevTaskId() + "' wasn't found!", HttpStatus.NOT_FOUND));
        List<TaskEntity> subTasks = tasks.stream()
                .filter(t -> !t.getId().equals(taskCreateRequest.getPrevTaskId()))
                .toList();

        /*
         * If prevTask is a parent task:
         * We're inserting a new task below it. Note that 'prevTask.getNextTask()' could be null, which just means the
         * newly created task will be at the tail of the entire task list associated with prevTask.
         *
         * NOTE: Since prevTask is a parentTask, the next task after it should be one of its subtasks. We should be able
         * to obtain nextTask, which is already in the list of subtasks. This scenario represents the idea of creating a
         * new subtask below the parentTask, but above its first subtask.
         */
        if (!subTasks.isEmpty()) {
            // Safely retrieve the nextTask based on the prevTask's nextTaskId
            TaskEntity nextTask = subTasks.stream()
                    .filter(t -> t.getId().equals(prevTask.getNextTaskId())) // Ensure correct parentheses
                    .findFirst()
                    .orElseThrow(() -> new ServiceException(
                            "Expected next task with ID '" + prevTask.getNextTaskId() +
                                    "' was not found in the list of subtasks for task with ID '" + prevTask.getId() + "'. " +
                                    "This likely indicates a server-side data inconsistency.",
                            HttpStatus.INTERNAL_SERVER_ERROR)
                    );
            TaskEntity newTask = createTaskAsSubTask(prevTask, prevTask, nextTask, taskCreateRequest);
            return taskMapper.toDTO(newTask);
        }

        /*
         * If we're creating a subtask:
         *
         * NOTE: A subtask cannot also be a parent task.
         */
        if (taskCreateRequest.isSubTask()) {
            // If prevTask isn't null (prevTask is a subtask to an existing parentTask), then throw an error
            if (prevTask.getParentTaskId() != null) {
                throw new ServiceException("You can't create a subtask for an already existing subtask!", HttpStatus.BAD_REQUEST);
            }

            // Attempt to find the task following prevTask; note this could be null, in the case where prevTask is at the tail
            TaskEntity nextTask = taskRepository.findById(prevTask.getNextTaskId()).orElse(null);


            // At this point, we know prevTask isn't a parentTask, and it isn't a subTask, it's a regular task
            TaskEntity newTask = createTaskAsSubTask(prevTask, prevTask, nextTask, taskCreateRequest);
            return taskMapper.toDTO(newTask);
        }

        // Get the next task; nextTask could be null in the case where the newly created task is going to become the tail of the task list.
        TaskEntity nextTask = taskRepository.findById(prevTask.getNextTaskId()).orElse(null);
        /*
         * If prevTask and its next task share the same parent:
         * Insert the new task as a subtask that references that parent, placed between prevTask and its next task.
         *
         * NOTE: First check that nextTask isn't null. If it isn't null then we can call the getParentTask() method on
         * it without running into any null pointer exceptions!
         */
        if (nextTask != null && prevTask.getParentTaskId().equals(nextTask.getParentTaskId())) {
            TaskEntity parentTask = taskRepository.findById(prevTask.getParentTaskId()).orElse(null);
            TaskEntity newTask = createTaskAsSubTask(parentTask, prevTask, nextTask, taskCreateRequest);
            return taskMapper.toDTO(newTask);
        }

        /*
         * Default case: Insert the new task after a base task or a subtask.
         * - The new task is not a subtask and will be placed after prevTask.
         * - If prevTask has no next task, the new task becomes the tail.
         */
        TaskEntity newTask = TaskEntity.builder()
                .taskListId(prevTask.getTaskListId())
                // Here we don't pass in parentTask because we're not creating a subtask
                .prevTaskId(prevTask.getId())
                // nextTask could be null, which indicates that 'newTask' is being created at the tail of the taskList
                .nextTaskId(nextTask != null ? nextTask.getId() : null)
                .title(taskCreateRequest.getTitle())
                .description(taskCreateRequest.getDescription())
                .isComplete(taskCreateRequest.isComplete())
                .isAllDay(taskCreateRequest.isAllDay())
                .isStarred(taskCreateRequest.isStarred())
                .dueDate(taskCreateRequest.getDueDate())
                .build();

        /*
         * 1. Save the newTask to the database to assign it an ID.
         * 2. Update the pointers for prevTask and nextTask, if they are defined.
         *    - Set prevTask's nextTask to the new task.
         *    - If nextTask exists, set its prevTask to the new task.
         * 3. Save them to the database (the ones that weren't null), and return the newly created task back as a DTO
         * 4. Return the newly created task as a DTO.
         */
        newTask = taskRepository.save(newTask);
        prevTask.setNextTaskId(newTask.getId());
        if (nextTask != null) {
            nextTask.setPrevTaskId(newTask.getId());
        }

        List<TaskEntity> combinedTasks = new ArrayList<>();
        combinedTasks.add(prevTask);
        combinedTasks.add(nextTask);
        taskRepository.saveAll(filterUniqueNonNullTasks(combinedTasks));
        return taskMapper.toDTO(newTask);
    }

    @Override
    public TaskDTO updateById(Long id, TaskUpdateRequest taskUpdateRequest) throws ServiceException {
        TaskEntity targetTask = getTaskEntity(id);

        // Check if any of the task 'content' related properties have changed; if they have we update them
        if (!Objects.equals(targetTask.getTitle(), taskUpdateRequest.getTitle()) ||
                !Objects.equals(targetTask.getDescription(), taskUpdateRequest.getDescription()) ||
                targetTask.isComplete() != taskUpdateRequest.isComplete() ||
                targetTask.isAllDay() != taskUpdateRequest.isAllDay() ||
                targetTask.isStarred() != taskUpdateRequest.isStarred() ||
                !Objects.equals(targetTask.getDueDate(), taskUpdateRequest.getDueDate())) {
            targetTask.setTitle(taskUpdateRequest.getTitle());
            targetTask.setDescription(taskUpdateRequest.getDescription());
            targetTask.setComplete(taskUpdateRequest.isComplete());
            targetTask.setAllDay(taskUpdateRequest.isAllDay());
            targetTask.setStarred(taskUpdateRequest.isStarred());
            targetTask.setDueDate(taskUpdateRequest.getDueDate());
            taskRepository.save(targetTask);
        }
        return taskMapper.toDTO(targetTask);
    }

    @Override
    public TaskDTO findById(Long id) throws ServiceException {
        TaskEntity task = getTaskEntity(id);
        return taskMapper.toDTO(task);
    }
}

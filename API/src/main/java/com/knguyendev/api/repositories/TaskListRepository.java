package com.knguyendev.api.repositories;

import com.knguyendev.api.domain.entities.TaskListEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskListRepository extends CrudRepository<TaskListEntity, Long> {


    /**
     * Gets all taskLists associated with a given user. Mainly used when you want to load
     * in the task lists associated with a user.
     * @param userId ID of the user being referenced in the taskList
     * @return A list of TaskList entities that reference the user.
     */
    List<TaskListEntity> findByUserId(Long userId);


    /*
     * SELECT * FROM TaskList tl
     *
     *
     * @param taskListId
     * @return
     */
//    @Query(value = "SELECT * FROM TaskList tl LEFT JOIN Task t ON tl.id = t.taskListId WHERE tl.id = :taskListId", nativeQuery = true)
//    List<Object[]> findTaskListWithTasksNative(@Param("taskListId") Long taskListId);


    /**
     * Deletes all TaskList that reference a specific userId. So it will delete
     * all task lists for a given user. This should only be used when a user is deleted since this
     * would also delete the default list.
     * @param userId ID of the user whose task lists we are deleting
     */
    @Modifying
    @Query("DELETE FROM TaskListEntity t WHERE t.userId = :userId")
    void deleteByUserId(Long userId);



}

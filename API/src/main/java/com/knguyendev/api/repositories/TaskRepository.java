package com.knguyendev.api.repositories;

import com.knguyendev.api.domain.entities.TaskEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends CrudRepository<TaskEntity, Long> {

    @Query("SELECT t FROM TaskEntity t WHERE t.taskListId = :taskListId")
    List<TaskEntity> findByTaskListId(Long taskListId);

    @Query("SELECT t FROM TaskEntity t WHERE t.taskListId = :taskListId AND t.prevTaskId IS NULL")
    Optional<TaskEntity> findTaskListHeadByTaskListId(@Param("taskListId") Long taskListId);

    // Still need to test this
    @Query("SELECT t FROM TaskEntity t WHERE t.parentTaskId = :parentTaskId")
    List<TaskEntity> findByParentTaskId(Long parentTaskId);

    // Finds a task, and then its prev and next tasks
    @Query("SELECT t FROM TaskEntity t " +
            "WHERE t.id = :id OR " +
            "t.prevTaskId = :id OR " +
            "t.nextTaskId = :id")
    List<TaskEntity> findTaskPrevNext(@Param("id") Long id);


    // Custom query, for getting a task and its subTasks
    @Query("SELECT t FROM TaskEntity t WHERE t.id = :id OR t.parentTaskId = :id")
    List<TaskEntity> findTaskAndSubtasksById(@Param("id") Long id);

    @Query("SELECT t FROM TaskEntity t WHERE t.id IN :ids")
    List<TaskEntity> findAllByIds(@Param("ids") List<Long> ids);

    // Deletes a parentTask and its subTasks
    @Modifying
    @Query("DELETE FROM TaskEntity t WHERE t.parentTaskId = :id OR t.id = :id")
    void deleteTaskAndSubTasksById(@Param("id") Long id);

    @Modifying
    void deleteByTaskListId(Long taskListId);


    /**
     * Deletes all tasks related to a user
     * @param userId ID of the user whose task lists we are deleting
     */
    @Modifying
    @Query("DELETE FROM TaskEntity t WHERE t.taskListId IN (SELECT tl.id FROM TaskListEntity tl WHERE tl.userId = :userId)")
    void deleteByUserId(Long userId);

    @Query("SELECT t from TaskEntity t WHERE t.taskListId IN (SELECT tl.id FROM TaskListEntity tl WHERE tl.userId = :userId)")
    List<TaskEntity> findByUserId(Long userId);


}
/*

1. Reduce the Number of Queries
Batch Operations: Instead of making multiple individual queries (e.g., one query per row to update), try to batch them into a single query where possible.
Joins and Subqueries: Use joins or subqueries to fetch related data in a single query rather than making multiple round trips to the database.
Caching: Cache results of frequently accessed data that does not change often to reduce database hits.
2. Optimize Query Performance
Indexes: Ensure that your database tables have appropriate indexes, particularly on columns that are frequently used in WHERE, JOIN, and ORDER BY clauses.
Selective Columns: Only select the columns you need. Fetching unnecessary data can slow down your queries and increase the amount of data transferred.
Limit and Pagination: When dealing with large datasets, use LIMIT and pagination to avoid pulling too much data at once.
3. Reduce the Number of Rows Processed
Filtering Early: Apply filters (WHERE clauses) as early as possible to reduce the number of rows the database needs to process.
Proper Use of Aggregate Functions: Use aggregate functions (e.g., COUNT, SUM, AVG) efficiently to avoid processing unnecessary data.
4. Consider Read vs. Write Optimization
Read Optimization: Focus on optimizing reads if your application is read-heavy. This involves caching, using read replicas, and optimizing query plans.
Write Optimization: For write-heavy applications, focus on reducing transaction locks, optimizing bulk inserts/updates, and using asynchronous writes where possible.
5. Transaction Management
Minimize Transaction Scope: Keep transactions as short as possible to reduce the risk of locking and improve concurrency.
Use Proper Isolation Levels: Choose the right isolation level for your transactions to balance between data consistency and performance.
6. Avoid N+1 Query Problems
Lazy vs. Eager Loading: Be mindful of when to use lazy loading (fetch data only when needed) versus eager loading (fetch all related data at once) to avoid the N+1 query problem.
7. Monitor and Profile Queries Profiling: Use tools like EXPLAIN in SQL to analyze how your queries are executed and identify bottlenecks.
Database Monitoring: Regularly monitor your database performance, looking for slow queries and optimizing them.
8. Optimize for Scalability
Partitioning and Sharding: For very large datasets, consider partitioning tables or sharding databases to distribute the load.
Database Replication: Use replication for scaling reads and ensuring high availability.

 */
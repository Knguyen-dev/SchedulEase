package com.knguyendev.api.domain.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "Task")
public class TaskEntity {

    // Unique identifier for a given task
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    // Foreign key representing the taskListId, indicating which TaskList this Task belongs to; needed for cascade deletion
    @Column(name="taskListId", nullable = false)
    private Long taskListId;

    /*
     * Design Decision: Using `Long` IDs for Self-Referencing Foreign Keys in `TaskEntity`
     *
     * Context:
     * In our application, tasks can have hierarchical relationships (e.g., subtasks).
     * Additionally, tasks can have sequential relationships where each task can reference
     * a previous or next task. Initially, we considered using `TaskEntity` references
     * to model these relationships. However, we encountered challenges related to
     * deeply nested relationships, circular references, and overall code complexity.
     *
     * Decision:
     * We decided to replace `TaskEntity` references with `Long` IDs for the following fields:
     * - `parentTaskId`: Represents the ID of the parent task (for subtasks).
     * - `prevTaskId`: Represents the ID of the previous task in a sequence.
     * - `nextTaskId`: Represents the ID of the next task in a sequence.
     *
     * Reasons:
     * 1. Avoiding Deeply Nested Relationships:
     *    - Using `TaskEntity` references directly can lead to deeply nested relationships,
     *      making the code difficult to understand and maintain. Each task potentially
     *      holds references to multiple other tasks, creating complex and intertwined object graphs.
     *    - By using `Long` IDs, we simplify the relationships to a flat structure of IDs,
     *      which are easier to manage and reason about.
     *
     * 2. Simplifying Testing:
     *    - Testing becomes more straightforward with `Long` IDs since we're dealing with
     *      primitive data types rather than complex entity objects. It reduces the need for
     *      extensive mocking of nested objects and makes tests easier to write and maintain.
     *
     * 3. Reducing Risk of Recursive Issues:
     *    - Self-referencing entities can introduce the risk of recursive loops, leading to
     *      issues like `StackOverflowError` during serialization, or when calling methods like
     *      `toString`, `equals`, or `hashCode`.
     *    - Using `Long` IDs avoids these recursive problems, leading to a more robust and reliable system.
     *
     * 4. Improving Code Clarity:
     *    - With `Long` IDs, the relationships between tasks are explicitly managed, making the
     *      flow of logic clearer. Developers can easily see how tasks relate to one another
     *      without navigating through multiple levels of entity references.
     *
     * 5. Better Control Over Queries:
     *    - The use of `Long` IDs allows for more precise control over database queries. Instead
     *      of relying on JPA to manage relationships, we explicitly define queries to retrieve
     *      the necessary data, which can be optimized for performance.
     *
     * Considerations:
     * - Manual Management of Relationships:
     *   - The responsibility for maintaining relationships and ensuring data integrity now
     *     lies in the service layer, rather than relying on PA's cascading and orphan removal features.
     * - Loss of Automatic JPA Features:
     *   - Some convenience features provided by JPA for entity relationships are lost, such
     *     as automatic fetching and cascading operations. These need to be handled manually in the code.
     *
     * Conclusion:
     * Switching to `Long` IDs for self-referencing foreign keys is a strategic decision aimed at
     * simplifying our task management logic, making the codebase easier to understand, test, and
     * maintain. While it introduces some manual management of relationships, the trade-offs in
     * terms of clarity and robustness are well worth it.
     */
    /**
     * Self-referencing foreign key representing the parentTaskId, which allows for the creation of sub-tasks.
     * A task may not have a parentTask, in which case parentTask will be null.
     */
    @Column(name = "parentTaskId")
    private Long parentTaskId;

    /*
     * Foreign key referencing the previous Task in a sequence.
     * This is optional as a task might not have a predecessor.
     */
    @Column(name = "prevTaskId")
    private Long prevTaskId;

    /*
     * Foreign key referencing the next Task in a sequence.
     * This is optional as a task might not have a successor.
     */
    @Column(name = "nextTaskId")
    private Long nextTaskId;

    // The title of the task, this is a required field with a maximum length of 100 characters.
    @Column(name="title", columnDefinition="VARCHAR(100) NOT NULL")
    private String title;

    /*
     * A description of the task, which is optional.
     * This field can be null, as a blank or empty description will be treated as null.
     * We chose not to set a default value of an empty string to avoid ambiguity between a missing and an intentionally blank description.
     */
    @Column(name="description", columnDefinition="VARCHAR(300)")
    private String description;

    // Boolean flag indicating whether the task is complete. Default is false.
    @Column(name="isComplete", columnDefinition="BOOLEAN NOT NULL DEFAULT FALSE")
    private boolean isComplete = false;

    // Boolean flag indicating whether the task is starred or marked as important. Default is false.
    @Column(name="isStarred", columnDefinition="BOOLEAN NOT NULL DEFAULT FALSE")
    private boolean isStarred = false;

    // Boolean flag indicating whether the task is an all-day task. Default is false.
    @Column(name="isAllDay", columnDefinition="BOOLEAN NOT NULL DEFAULT FALSE")
    private boolean isAllDay = false;

    /*
     * Due date of the task in UTC (Universal Time Coordinated). This field is optional.
     * We don't store a separate timezone identifier because tasks are typically created relative to the user's timezone,
     * and storing the due date in UTC allows for consistent and universal handling of time across different timezones.The
     *
     * + For our TaskEntity:
     * Use Case: Ideal for tasks, reminders, or events that should be adjusted to the user's current time zone, regardless of where they are in the world.
     * Example: A task is due at 12:00 PM EST on Friday. If the user travels to California, the task adjusts to 9:00 AM PST on Friday.
     * Advantage: This ensures consistency and correct display relative to the user's current time zone.
     *
     * + For use later when making calendar:
     * Use Case: Necessary for events that are tied to a specific local time, regardless of the user's current location.
     * Example: Booking a restaurant in California for 6:00 PM PST. Even if the user is in Indiana, they want the event to remain at 6:00 PM PST, not adjusted to Indiana time.
     * Advantage: This preserves the intended local time of the event, preventing any confusion or incorrect scheduling.
     */
    @Column(name="dueDate", columnDefinition="TIMESTAMP")
    private ZonedDateTime dueDate;
}

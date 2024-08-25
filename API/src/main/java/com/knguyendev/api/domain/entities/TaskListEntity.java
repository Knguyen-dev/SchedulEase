package com.knguyendev.api.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "TaskList")
public class TaskListEntity {

    // Unique identifier for a task list
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    // ID of the user that created and owns the task list
    @Column(name="userId", nullable = false)
    private Long userId;

    // Name of the task list; think of it as the title
    @Column(name="name", columnDefinition="VARCHAR(100) NOT NULL")
    private String name;

    // Whether the task list is the default one that every user should have.
    @Column(name="isDefault", columnDefinition="BOOLEAN NOT NULL DEFAULT FALSE")
    boolean isDefault = false;




    // Note: The `addTask` and `removeTask` methods are not strictly necessary for JPA to function correctly,
    // but they are useful for maintaining bidirectional consistency in the relationship. However, for our use case,
    // we are not using these methods because adding and removing tasks involves complex logic such as managing
    // task ordering within the task list. This complexity is handled by service functions rather than directly
    // through the entity methods.

    // Service functions will manage task ordering and other related operations, while entity methods would
    // only handle basic bidirectional consistency. This approach helps to keep the entity logic simpler and
    // delegates complex operations to the appropriate service layer.

    // In the future, if we encounter issues or need to simplify task management, we might revisit adding these
    // methods or implementing similar logic in the entity itself.
}
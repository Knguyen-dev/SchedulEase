package com.knguyendev.api.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "TaskList")
public class TaskListEntity {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name="userId", nullable = false)
    private Long userId;

    @Column(name="name", columnDefinition="VARCHAR(100) NOT NULL")
    private String name;

    @Column(name="isDefault", columnDefinition="BOOLEAN NOT NULL DEFAULT FALSE")
    boolean isDefault = false;
}


/**
 * Storing as a userId because we don't need the entire entity for our operations. And doing that multiple times for
 * many task lists would be dreadful. The only downside is that we'll probably have to execute multiple repository
 * methods for deleting, but that's pretty manageable:
 * 1. If user is deleted, we'll delete the corresponding TaskList rows. (a user can have many taskLists)
 * 2. If TaskList is deleted, we'll delete the corresponding Task rows. (a tasklist can have many tasks)
 */
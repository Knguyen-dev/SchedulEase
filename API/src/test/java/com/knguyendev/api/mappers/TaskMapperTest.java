package com.knguyendev.api.mappers;


import com.knguyendev.api.domain.dto.Task.TaskDTO;
import com.knguyendev.api.domain.dto.TaskList.TaskListDTO;
import com.knguyendev.api.domain.entities.TaskEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TaskMapperTest {

    private final TaskMapper taskMapper;

    @Autowired
    public TaskMapperTest(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Test
    public void testToDTO() {
        // Arrange
        TaskEntity entity = new TaskEntity();
        entity.setId(1L);
        entity.setTaskListId(2L);
        entity.setParentTaskId(3L);
        entity.setPrevTaskId(4L);
        entity.setNextTaskId(5L);
        entity.setTitle("Sample Task");
        entity.setDescription("This is a description");
        entity.setComplete(true);
        entity.setStarred(false);
        entity.setAllDay(true);
        entity.setDueDate(ZonedDateTime.now(ZoneId.of("UTC")));

        // Act
        TaskDTO dto = taskMapper.toDTO(entity);

        // Assert
        assertNotNull(dto);
        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getTaskListId(), dto.getTaskListId());
        assertEquals(entity.getParentTaskId(), dto.getParentTaskId());
        assertEquals(entity.getPrevTaskId(), dto.getPrevTaskId());
        assertEquals(entity.getNextTaskId(), dto.getNextTaskId());
        assertEquals(entity.getTitle(), dto.getTitle());
        assertEquals(entity.getDescription(), dto.getDescription());
        assertTrue(dto.isComplete());
        assertFalse(dto.isStarred());
        assertTrue(dto.isAllDay());
        assertEquals(entity.getDueDate(), dto.getDueDate());
    }


}

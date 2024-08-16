package com.knguyendev.api.mappers;


import com.knguyendev.api.domain.dto.TaskList.TaskListDTO;
import com.knguyendev.api.domain.entities.TaskListEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TaskListMapperTest {
    private final TaskListMapper taskListMapper;
    @Autowired
    public TaskListMapperTest(TaskListMapper taskListMapper) {
        this.taskListMapper = taskListMapper;
    }

    @Test
    void testEntityToDTO() {
        TaskListEntity taskList = TaskListEntity.builder()
                .id(1L)
                .userId(1L)
                .name("A cool task list")
                .isDefault(false)
                .build();

        TaskListDTO taskListDTO = taskListMapper.toDTO(taskList);

        // Assert that values are good
        assertEquals(taskList.getId(), taskListDTO.getId());
        assertEquals(taskList.getUserId(), taskListDTO.getUserId());
        assertEquals(taskList.getName(), taskListDTO.getName());
        assertEquals(taskList.isDefault(), taskListDTO.isDefault());
    }

}

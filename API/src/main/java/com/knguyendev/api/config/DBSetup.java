package com.knguyendev.api.config;

import com.knguyendev.api.domain.dto.ItemColor.ItemColorCreateDTO;
import com.knguyendev.api.domain.dto.Task.TaskCreateRequest;
import com.knguyendev.api.domain.dto.TaskList.TaskListDTO;
import com.knguyendev.api.domain.dto.TaskList.TaskListRequest;
import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.domain.entities.ItemColorEntity;
import com.knguyendev.api.domain.entities.TaskEntity;
import com.knguyendev.api.domain.entities.TaskListEntity;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.enumeration.UserRole;
import com.knguyendev.api.repositories.ItemColorRepository;
import com.knguyendev.api.repositories.TaskListRepository;
import com.knguyendev.api.repositories.TaskRepository;
import com.knguyendev.api.repositories.UserRepository;
import com.knguyendev.api.services.AuthService;
import com.knguyendev.api.services.ItemColorService;
import com.knguyendev.api.services.TaskListService;
import com.knguyendev.api.services.TaskService;
import com.knguyendev.api.utils.ValidationUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Don't want this setup function running and affecting my integration or unit tests though
@Component
public class DBSetup implements ApplicationRunner {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TaskListRepository taskListRepository;
    private final TaskRepository taskRepository;
    private final ItemColorRepository itemColorRepository;
    public DBSetup(PasswordEncoder passwordEncoder, UserRepository userRepository, TaskListRepository taskListRepository, TaskRepository taskRepository, ItemColorRepository itemColorRepository, ItemColorService itemColorService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.taskListRepository = taskListRepository;
        this.taskRepository = taskRepository;
        this.itemColorRepository = itemColorRepository;
    }


    private UserEntity createUserEntity(Long id, String username, String email, String firstName, String lastName, String biography, Boolean isVerified, String plainTextPassword, ZonedDateTime createdAt, UserRole role) {
        String encodedPassword = passwordEncoder.encode(plainTextPassword);
        return UserEntity.builder()
                .id(id)
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .biography(biography)
                .isVerified(isVerified)
                .password(encodedPassword)
                .createdAt(createdAt)
                .role(role)
                .build();
    }

    private UserEntity createUserA() {
        // Sample data for the first user
        Long USER_ID = 1L;
        String USER_USERNAME = "knguyen44";
        String USER_EMAIL = "knguyen44@gmail.com";
        String USER_FIRST_NAME = "Kevin";
        String USER_LAST_NAME = "Nguyen";
        String USER_BIOGRAPHY = "Hi I'm a content creator on Youtube and sometimes I stream on Twitch";
        Boolean USER_IS_VERIFIED = true;
        String USER_PASSWORD = "P$ssword_123";
        ZonedDateTime USER_CREATED_AT = ZonedDateTime.now(ZoneId.of("UTC"));
        UserRole USER_ROLE = UserRole.SUPER_ADMIN;
        return createUserEntity(
                USER_ID,
                USER_USERNAME,
                USER_EMAIL,
                USER_FIRST_NAME,
                USER_LAST_NAME,
                USER_BIOGRAPHY,
                USER_IS_VERIFIED,
                USER_PASSWORD,
                USER_CREATED_AT,
                USER_ROLE
        );
    }

    private UserEntity createUserB() {
        // Sample data for the second user
        Long USER_ID = 2L;
        String USER_USERNAME = "kbizzzyy";
        String USER_EMAIL = "kbizzzyy@gmail.com";
        String USER_FIRST_NAME = "Kon";
        String USER_LAST_NAME = "Sempton";
        String USER_BIOGRAPHY = "Hi someone that's doing a lot of stuff";
        Boolean USER_IS_VERIFIED = true;
        String USER_PASSWORD = "P$ssword_123";
        ZonedDateTime USER_CREATED_AT = ZonedDateTime.now(ZoneId.of("UTC"));
        UserRole USER_ROLE = UserRole.ADMIN;
        return createUserEntity(
                USER_ID,
                USER_USERNAME,
                USER_EMAIL,
                USER_FIRST_NAME,
                USER_LAST_NAME,
                USER_BIOGRAPHY,
                USER_IS_VERIFIED,
                USER_PASSWORD,
                USER_CREATED_AT,
                USER_ROLE
        );
    }

    private TaskListEntity createTaskListEntity(Long id, Long userId, String name, boolean isDefault) {
        return TaskListEntity.builder()
                .id(id)
                .userId(userId)
                .name(name)
                .isDefault(isDefault)
                .build();
    }

    private TaskListEntity createTaskListA() {
        Long id = 1L;
        Long userId = createUserA().getId();
        String name = "My Task List";
        boolean isDefault = true;
        return createTaskListEntity(id, userId, name, isDefault);
    }

    private TaskListEntity createTaskListB() {
        Long id = 2L;
        Long userId = createUserB().getId();
        String name = "My Task List";
        boolean isDefault = true;
        return createTaskListEntity(id, userId, name, isDefault);
    }


    public void loadUsers() {
        List<UserEntity> users = List.of(
                createUserA(),
                createUserB()
        );
        userRepository.saveAll(users);
    }

    public void loadTaskListsAndTasks() {
        TaskListEntity taskListA = createTaskListA();
        TaskListEntity taskListB = createTaskListB();
        taskListRepository.saveAll(List.of(
                taskListA,
                taskListB
        ));


    }

    public void loadItemColors() {

        List<ItemColorEntity> itemColors = List.of(
                ItemColorEntity.builder()
                        .id(1L)
                        .name("Flamingo")
                        .hexCode("#f28b82")
                        .build(),
                ItemColorEntity.builder()
                        .id(2L)
                        .name("Tomato")
                        .hexCode("#fbbc05")
                        .build(),
                ItemColorEntity.builder()
                        .id(3L)
                        .name("Banana")
                        .hexCode("#fff475")
                        .build(),
                ItemColorEntity.builder()
                        .id(4L)
                        .name("Tangerine")
                        .hexCode("#ffab91")
                        .build(),
                ItemColorEntity.builder()
                        .id(5L)
                        .name("Peacock")
                        .hexCode("#aecbfa")
                        .build(),
                ItemColorEntity.builder()
                        .id(6L)
                        .name("Graphite")
                        .hexCode("#e6c9ff")
                        .build(),
                ItemColorEntity.builder()
                        .id(7L)
                        .name("Blueberry")
                        .hexCode("#d7aaff")
                        .build(),
                ItemColorEntity.builder()
                        .id(8L)
                        .name("Basil")
                        .hexCode("#fdcfe8")
                        .build()
        );
        itemColorRepository.saveAll(itemColors);
    }

    // Is this going to run after it gets its dependencies?
    @Override
    public void run(ApplicationArguments args) {
        // I just won't run these for now. I'll only run them when I want to populate the database

        loadUsers();
        loadTaskListsAndTasks();
//        loadItemColors();
    }
}

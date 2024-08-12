package com.knguyendev.api.config;

import com.knguyendev.api.domain.dto.ItemColor.ItemColorCreateDTO;
import com.knguyendev.api.domain.dto.User.UserRegistrationDTO;
import com.knguyendev.api.enumeration.UserRole;
import com.knguyendev.api.services.AuthService;
import com.knguyendev.api.services.ItemColorService;
import com.knguyendev.api.utils.ValidationUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Don't want this setup function running and affecting my integration or unit tests though
@Component
public class DBSetup implements ApplicationRunner {

    private final ItemColorService itemColorService;
    private final AuthService authService;

    public DBSetup(ItemColorService itemColorService, AuthService authService) {
        this.itemColorService = itemColorService;
        this.authService = authService;
    }

    public void loadUsers() {
        List<UserRegistrationDTO> registrations = List.of(
                UserRegistrationDTO.builder()
                        .username("knguyen44")
                        .email("knguyen44@gmail.com")
                        .firstName("Kevin")
                        .lastName("Nguyen")
                        .password("P$ssword_123")
                        .build(),
                UserRegistrationDTO.builder()
                        .username("KbizzzyyCentral")
                        .email("KbizzzyyCentral@gmail.com")
                        .firstName("Kevin")
                        .lastName("Nguyen")
                        .password("P$ssword_123")
                        .build(),
                UserRegistrationDTO.builder()
                        .username("SkyKbean")
                        .email("SkyKbean@gmail.com")
                        .firstName("Skylar")
                        .lastName("Johnson")
                        .password("P$ssword_123")
                        .build()
        );
        List<UserRole> roles = List.of(
                UserRole.SUPER_ADMIN,
                UserRole.ADMIN,
                UserRole.USER
        );

        // Iterate via index to process each registration with its corresponding role
        for (int i = 0; i < registrations.size(); i++) {
            UserRegistrationDTO registration = registrations.get(i);
            UserRole role = roles.get(i);

            try {
                // Manually validate the user registration DTO
                ValidationUtil.validate(registration);

                // Normalize the data
                registration.normalizeData();

                // Register the user with the corresponding role
                authService.registerUser(registration, role);
            } catch (Exception e) {
                System.err.println("Error registering user '" + registration.getUsername() + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }


    public void loadItemColors() {

        Map<String, String> colorMap = new HashMap<>();

        // Adding Google Calendar colors to the map
        colorMap.put("Flamingo", "#f28b82");
        colorMap.put("Tomato", "#fbbc05");
        colorMap.put("Banana", "#fff475");
        colorMap.put("Tangerine", "#ffab91");
        colorMap.put("Peacock", "#aecbfa");
        colorMap.put("Graphite", "#e6c9ff");
        colorMap.put("Blueberry", "#d7aaff");
        colorMap.put("Basil", "#fdcfe8");

        for (String colorName : colorMap.keySet()) {
            ItemColorCreateDTO createItemColor = ItemColorCreateDTO.builder()
                    .name(colorName)
                    .hexCode(colorMap.get(colorName))
                    .build();

            try {
                // Manually validate
                ValidationUtil.validate(createItemColor);

                // Normalize the data
                createItemColor.normalizeData();

                // Call service function
                itemColorService.create(createItemColor);
            } catch (IllegalArgumentException e) {
                System.err.println("Validation failed for color '" + colorName + "': " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error creating item color '" + colorName + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Is this going to run after it gets its dependencies?
    @Override
    public void run(ApplicationArguments args) {

        // I just won't run these for now. I'll only run them when I want to populate the database
        loadUsers();
        loadItemColors();


    }
}

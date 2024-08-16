package com.knguyendev.api.domain.dto.TaskList;

import com.knguyendev.api.domain.dto.TaskList.constraints.NameConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the request payload for creating a new TaskList.
 * <p>
 * In this design, certain fields have been omitted based on application-specific requirements and logic:
 * <p>
 * - userId: The `userId` field is excluded because the task list is created for the authenticated user.
 *   The backend already handles the association between the task list and the user, ensuring that a user
 *   cannot create task lists for other users. This enhances security and simplifies the API payload.
 * <p>
 * - isDefault: The `isDefault` field is also omitted as the application design dictates that only one
 *   default task list is created automatically when a user creates their account. Additional task lists
 *   created by the user will not be default. This simplifies the creation process and avoids unnecessary
 *   complexity in the user input.
 * <p>
 * The only field included is `name`, which is the title that the user will input for their new task list.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskListRequest {

    // Name/title the user will input for the TaskList
    @NameConstraint
    private String name;

    public void normalizeData() {
        // we want to trim any whitespace around the name
        this.name = name.trim();
    }
}

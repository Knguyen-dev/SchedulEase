package com.knguyendev.api.services;


import com.knguyendev.api.domain.dto.User.ChangePasswordDTO;
import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.User.UserDeleteDTO;
import com.knguyendev.api.domain.dto.User.UserProfileUpdateDTO;
import com.knguyendev.api.exception.ServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Let's define an interface for our UserService. With this you can define some
 * service or business-logic related functions (relating to the persistence layer anyway).
 * Then all of your implementations need to align with this interface. This makes it easy
 * to organize things and abstract logic away.
 *
 *
 */
public interface UserService {

    /**
     * Finds all users and returns them as a list of UserDTOs.
     */
    List<UserDTO> findAll();

    /**
     * Updates a user by its ID. This should be used for updating a user's profile information
     * @param id I'd of the user that we're trying to find
     * @return DTO representation of the user we're trying to find.
     * @throws ServiceException An error to indicate that we didn't find a user with that id.
     */
    UserDTO findById(Long id) throws ServiceException;


    UserDTO getAuthenticatedUser();

    /**
     * Updates the profile of the currently authenticated user
     * @param userProfileUpdateDTO DTO that contains all the profile information that's being updated.
     * @return Returns the DTO of the authenticated user after their profile has been updated.
     */
    UserDTO updateAccountProfile(UserProfileUpdateDTO userProfileUpdateDTO);


    /**
     * Deletes the account of the currently authenticated user and logs them out.
     * This service function handles the deletion of the user account associated with the currently authenticated user.
     * In addition to removing the user from the database, it also ensures that the user is logged out from the current session.
     * The method requires both the HttpServletRequest and HttpServletResponse objects to manage the logout process properly.
     *
     * @param request The HttpServletRequest object representing the client's request, needed for logout.
     * @param response The HttpServletResponse object representing the response to be sent to the client, needed for logout.
     * @return UserDTO representing the deleted user’s information.
     * @throws ServiceException if the user being deleted is not found or if there are issues during the deletion process.
     */
    UserDTO deleteAccount(HttpServletRequest request, HttpServletResponse response, UserDeleteDTO userDeleteDTO) throws ServiceException;


    /**
     * Deletes a user via its ID. More so used by admins as it allows admins to be able to delete the account of any
     * other user. As well as this, there's builtin logic to prevent the authenticated user from deleting themselves since
     * this service doesn't handle logging out the user also.
     * @param id ID of the user
     * @return DTO representing the deleted user
     * @throws ServiceException Thrown when a User wasn't found or any other Service-Layer related exception
     */
    UserDTO deleteById(Long id) throws ServiceException;


    /**
     * Service method for changing the password of the currently authenticated user.
     * This method performs the following tasks:
     * <ol>
     *     <li>Validates the current password provided by the user against the stored password.</li>
     *     <li>Updates the user's password in the database if the above conditions are met.</li>
     *     <li>Logout the user.</li>
     *     <li>Returns a UserDTO representing the updated user information.</li>
     * </ol>
     *
     * @param request The HTTP request object, which may be used for additional context or operations related to the request.
     * @param response The HTTP response object, which may be used for sending response-related information.
     * @param changePasswordDTO Data Transfer Object containing the current password, new password, and password confirmation.
     *                          It should include validation annotations to ensure proper input.
     * @return UserDTO A Data Transfer Object representing the updated user information, including the new password (hashed).
     * @throws ServiceException If the current password is incorrect, the new passwords do not match, or any other error occurs during the process.
     */
    UserDTO changePassword(HttpServletRequest request, HttpServletResponse response, ChangePasswordDTO changePasswordDTO);


    /*
     * We've yet to do this. It's not that important, as it more so involves us connecting to SendGrid or some
     * other email api. I'd rather focus on other application logic since we've gotten the main auth-related stuff seemingly done.
     */

    // resetPassword


    // verifyEmail



}

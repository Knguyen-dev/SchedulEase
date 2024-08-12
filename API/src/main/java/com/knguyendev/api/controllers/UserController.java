package com.knguyendev.api.controllers;

import com.knguyendev.api.domain.dto.User.ChangePasswordDTO;
import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.dto.User.UserDeleteDTO;
import com.knguyendev.api.domain.dto.User.UserProfileUpdateDTO;
import com.knguyendev.api.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/my_profile")
    public ResponseEntity<UserDTO> updateMyProfile(@RequestBody UserProfileUpdateDTO userProfileUpdateDTO) {

        userProfileUpdateDTO.normalizeData();

        return new ResponseEntity<>(userService.updateAccountProfile(userProfileUpdateDTO), HttpStatus.OK);
    }

    @GetMapping("/my_profile")
    public ResponseEntity<UserDTO> getMyProfile() {
        return new ResponseEntity<>(userService.getAuthenticatedUser(), HttpStatus.OK);
    }

    @PutMapping("/my_profile/changePassword")
    public ResponseEntity<UserDTO> changePassword(
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid @RequestBody ChangePasswordDTO changePasswordDTO
    ) throws MethodArgumentNotValidException {

        // Ensure the newPassword and confirmNewPassword match
        if (!changePasswordDTO.isNewPasswordMatch()) {
            BindingResult bindingResult = new BeanPropertyBindingResult(changePasswordDTO, "changePasswordDTO");
            FieldError fieldError = new FieldError("changePasswordDTO", "confirmPassword", "New password and confirm password fields don't match!");
            bindingResult.addError(fieldError);
            throw new MethodArgumentNotValidException(null, bindingResult);
        }

        return new ResponseEntity<>(userService.changePassword(request, response, changePasswordDTO), HttpStatus.OK);
    }

    @DeleteMapping("/my_profile")
    public ResponseEntity<UserDTO> deleteMyAccount(
            HttpServletRequest request,
            HttpServletResponse response,
            @Valid @RequestBody UserDeleteDTO userDeleteDTO
            ) throws MethodArgumentNotValidException {

        // If plain-text password fields don't match, throw a MethodArgumentNotValidException with a specific field and error message
        if (!userDeleteDTO.isPasswordMatch()) {
            BindingResult bindingResult = new BeanPropertyBindingResult(userDeleteDTO, "userDeleteDTO");
            FieldError fieldError = new FieldError("userDeleteDTO", "confirmPassword", "Passwords do not match!");
            bindingResult.addError(fieldError);
            throw new MethodArgumentNotValidException(null, bindingResult);
        }



        return new ResponseEntity<>(userService.deleteAccount(request, response, userDeleteDTO), HttpStatus.OK);
    }


    @GetMapping(path="/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    @DeleteMapping(path="/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SUPER_ADMIN')")
    public ResponseEntity<UserDTO> deleteUserById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.deleteById(id), HttpStatus.OK);
    }

    @GetMapping(path="")
    public ResponseEntity<List<UserDTO>> getUserList() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }
}

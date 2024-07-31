package com.knguyendev.api.controllers;

import com.knguyendev.api.domain.dto.User.UserDTO;
import com.knguyendev.api.domain.entities.UserEntity;
import com.knguyendev.api.mappers.Mapper;
import com.knguyendev.api.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class UserController {
    private UserService userService;
    private Mapper<UserEntity, UserDTO> userMapper;
    public UserController(UserService userService, Mapper<UserEntity, UserDTO> userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping(path="/users")
    public List<UserDTO> getAuthorList() {
        List<UserEntity> users = userService.findAll();

        // Need to convert all UserEntities to the dto versions.
        return users.stream().map(userMapper::mapTo).collect(Collectors.toList());
    }

    @GetMapping(path="/users/{username}")
    public ResponseEntity<UserDTO> getAuthor(@PathVariable("username") String username) {
        Optional<UserEntity> foundUser = userService.findByUsername(username);

        // If no user was found, then early return a 404 not found
        if (foundUser.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // Convert into Dto and send it back
        UserDTO userDto = userMapper.mapTo(foundUser.get());
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
}

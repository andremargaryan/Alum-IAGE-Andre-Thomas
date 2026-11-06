package com.example.backend.controller;

import com.example.backend.dto.UserRegistrationDto;
import com.example.backend.dto.UserResponseDto;
import com.example.backend.dto.UserUpdateDto;
import com.example.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRegistrationDto registration) {
        UserResponseDto user = userService.createUser(registration);
        return ResponseEntity.created(URI.create("/users/" + user.id())).body(user);
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable("id") Integer id) {
        return ResponseEntity.of(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(@PathVariable("id") Integer id, @Valid @RequestBody UserUpdateDto update) {
        return userService.updateUser(id, update);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

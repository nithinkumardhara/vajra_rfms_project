package com.vajraiot.VJ_RLY_RFMS_REST_APIs.controller;

import com.vajraiot.VJ_RLY_RFMS_REST_APIs.dto.*;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.entity.LoginCredentials;
import com.vajraiot.VJ_RLY_RFMS_REST_APIs.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/signup")
    public ResponseEntity<LoginCredentials> signup(@RequestBody LoginCredentialsDTO loginCredentialsDto) {
        LoginCredentials loginCredentials = userService.signUp(loginCredentialsDto);
        return ResponseEntity.ok(loginCredentials);
    }

    @PostMapping("/auth/create-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoginCredentials> createUser(@RequestBody LoginCredentialsDTO loginCredentialsDto) {
        LoginCredentials users = userService.createUser(loginCredentialsDto);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<LoginCredentialsDTO> getUsers() {
        return userService.getUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/edit/user")
    public ResponseEntity<LoginCredentialsDTO> updateUser(@RequestParam String username, @RequestBody LoginCredentialsDTO loginCredentialsDto) {
        return ResponseEntity.ok(userService.editUser(username,loginCredentialsDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/user")
    public ResponseEntity<String> deleteUser(@RequestParam String username) {
        return ResponseEntity.ok(userService.deleteUser(username));
    }

}

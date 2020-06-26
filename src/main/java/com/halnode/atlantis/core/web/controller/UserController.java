package com.halnode.atlantis.core.web.controller;

import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import com.halnode.atlantis.core.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userRepository.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> saveUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.saveUser(user));
    }

    @PutMapping
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        return ResponseEntity.ok(userRepository.save(user));
    }
}

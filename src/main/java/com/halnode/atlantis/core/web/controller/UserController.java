package com.halnode.atlantis.core.web.controller;

import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @NonNull
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @NonNull
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getUsers() {
        try {
            List<User> userList = userRepository.findAll();
            return ResponseEntity.ok(userList);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok("Failed to fetch users");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userRepository.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody User user) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User saved = userRepository.save(user);
        return ResponseEntity.ok(saved);
    }
}

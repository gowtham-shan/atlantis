package com.halnode.atlantis.core.web.controller;

import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import com.halnode.atlantis.core.service.TenantService;
import com.halnode.atlantis.core.service.UserDetailsServiceImpl;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @NonNull
    private final UserDetailsServiceImpl userService;

    @NonNull
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<?> register(@RequestBody User user) {
                String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User saved = userRepository.save(user);
        tenantService.initDatabase(user.getUserName());
        //AuthUser created = userService.createUser(user);
        return ResponseEntity.ok(saved);
    }
}

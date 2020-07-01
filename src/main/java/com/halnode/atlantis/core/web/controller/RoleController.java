package com.halnode.atlantis.core.web.controller;

import com.halnode.atlantis.core.persistence.model.Role;
import com.halnode.atlantis.core.persistence.repository.RoleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RestController
@RequestMapping("/admin/role")
@RequiredArgsConstructor
public class RoleController {

    @NonNull
    private final RoleRepository roleRepository;

    @GetMapping
    public ResponseEntity<?> getRoles(HttpServletRequest request) {
        System.out.println(request.getSession().getId() + request.getSession().getAttributeNames());
        return ResponseEntity.ok(roleRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role role) {
        Role created = roleRepository.save(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRole(@PathVariable Long id) {
        Optional<Role> role = roleRepository.findById(id);
        return ResponseEntity.ok(role.get());
    }

    @PutMapping
    public ResponseEntity<?> updateRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleRepository.save(role));
    }
}

package com.halnode.atlantis.core.web.controller;

import com.halnode.atlantis.core.exception.ResourceNotFoundException;
import com.halnode.atlantis.core.persistence.model.Role;
import com.halnode.atlantis.core.persistence.repository.RoleRepository;
import com.halnode.atlantis.core.service.RoleService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/role")
@RequiredArgsConstructor
public class RoleController {

    @NonNull
    private final RoleRepository roleRepository;

    @NonNull
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<?> getRoles() {
        return ResponseEntity.ok(roleService.getRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRole(@PathVariable Long id) {
        Optional<Role> role = roleRepository.findById(id);
        return ResponseEntity.ok(role.orElseThrow());
    }

    @PostMapping
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role role) {
        Role created = roleService.saveRole(role);
        return ResponseEntity.ok(created);
    }

    @PutMapping
    public ResponseEntity<?> updateRole(@Valid @RequestBody Role role) {
        return ResponseEntity.ok(roleService.updateRole(role));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        roleService.deleteRoleById(id);
        return ResponseEntity.ok().build();
    }
}

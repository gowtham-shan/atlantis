package com.halnode.atlantis.core.web.controller;

import com.halnode.atlantis.core.persistence.repository.PrivilegeRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/privilege/")
@RequiredArgsConstructor
public class PrivilegeController {

    @NonNull
    private final PrivilegeRepository privilegeRepository;

    @GetMapping
    public ResponseEntity<?> getAllPrivileges(){
        return ResponseEntity.ok(privilegeRepository.findAll());
    }
}

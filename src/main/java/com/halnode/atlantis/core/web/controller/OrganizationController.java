package com.halnode.atlantis.core.web.controller;

import com.halnode.atlantis.core.persistence.dto.OrganizationDTO;
import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.repository.OrganizationRepository;
import com.halnode.atlantis.core.service.OrganizationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

    @NonNull
    private final OrganizationRepository organizationRepository;

    @NonNull
    private final OrganizationService organizationService;
    
    @GetMapping
    public ResponseEntity<?> getOrganizations() {
        return ResponseEntity.ok(organizationRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> saveOrganization(@RequestBody OrganizationDTO organizationDTO) {
        return ResponseEntity.ok(organizationService.saveOrganization(organizationDTO));
    }

    @PutMapping
    public ResponseEntity<?> updateOrganization(@RequestBody Organization organization) {
        return ResponseEntity.ok(organizationRepository.save(organization));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrganization(@PathVariable Long id) {
        organizationRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

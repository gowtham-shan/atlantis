package com.halnode.atlantis.core.web.controller;

import com.halnode.atlantis.core.persistence.dto.OrganizationDTO;
import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.service.OrganizationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/admin/organization")
@RequiredArgsConstructor
public class OrganizationController {

    @NonNull
    private final OrganizationService organizationService;

    @GetMapping
    public ResponseEntity<?> getOrganizations() {
        return ResponseEntity.ok(organizationService.getOrganizations());
    }

    @PostMapping
    public ResponseEntity<?> createOrganization(@Valid @RequestBody OrganizationDTO organizationDTO) {
        return ResponseEntity.ok(organizationService.saveOrganization(organizationDTO));
    }

    @PutMapping
    public ResponseEntity<?> updateOrganization(@Valid @RequestBody Organization organization) {
        return ResponseEntity.ok(organizationService.updateOrganization(organization));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrganization(@PathVariable Long id) {
        organizationService.deleteOrganization(id);
        return ResponseEntity.ok().build();
    }
}

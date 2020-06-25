package com.halnode.atlantis.core.web.controller;

import com.halnode.atlantis.core.persistence.dto.OrganizationDTO;
import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.OrganizationRepository;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import com.halnode.atlantis.core.service.TenantService;
import com.halnode.atlantis.spring.SetupDataLoader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

    @NonNull
    private final OrganizationRepository organizationRepository;

    @NonNull
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final TenantService tenantService;

    @GetMapping
    public ResponseEntity<?> getOrganizations() {
        return ResponseEntity.ok(organizationRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> saveOrganization(@RequestBody OrganizationDTO organizationDTO) {
        Organization organization = organizationDTO.getOrganization();

        Organization created = organizationRepository.save(organizationDTO.getOrganization());
        List<User> usersList = organizationDTO.getUsersList();
        if (!ObjectUtils.isEmpty(usersList)) {
            usersList.forEach(user -> {
                String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
                user.setPassword(encodedPassword);
                user.setOrganization(created);
                SetupDataLoader.organizationSchemaMap.put(user.getUserName(), created.getName());
                userRepository.save(user);
            });
        }
        tenantService.initDatabase(organizationDTO.getOrganization().getName());
        return ResponseEntity.ok(created);
    }
}

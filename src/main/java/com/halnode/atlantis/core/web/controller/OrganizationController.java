package com.halnode.atlantis.core.web.controller;

import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.OrganizationRepository;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import com.halnode.atlantis.core.service.TenantService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/organization")
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
    public ResponseEntity<?> getOrganizations(){
        return ResponseEntity.ok(organizationRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<?> saveOrganization(@RequestBody Organization organization){
//        if(!ObjectUtils.isEmpty(organization.getUsers())){
//            organization.getUsers().stream().forEach(user -> {
//                String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
//                user.setPassword(encodedPassword);
//                user.setOrganization(organization);
//            });
//        }
//        Set<User> users=organization.getUsers();
//        Optional<User> optionalUser = users.stream().findFirst();
//        User user=optionalUser.get();
//        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
//        user.setPassword(encodedPassword);
//        User saved = userRepository.save(user);
        Organization created=organizationRepository.save(organization);
        tenantService.initDatabase(organization.getName());
        return ResponseEntity.ok(created);
    }
}

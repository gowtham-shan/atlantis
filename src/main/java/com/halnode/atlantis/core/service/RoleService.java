package com.halnode.atlantis.core.service;

import com.halnode.atlantis.core.persistence.model.Role;
import com.halnode.atlantis.core.persistence.repository.RoleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleService {

    @NonNull
    private final RoleRepository roleRepository;

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }
}

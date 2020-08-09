package com.halnode.atlantis.core.service;

import com.halnode.atlantis.core.persistence.model.Role;
import com.halnode.atlantis.core.persistence.repository.RoleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RoleService {

    @NonNull
    private final RoleRepository roleRepository;

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }

    public Role saveRole(Role role){
        return roleRepository.save(role);
    }

    public Role updateRole(Role newRole){
        Optional<Role> role=roleRepository.findById(newRole.getId());
        if(role.isPresent()){
            Role roleFromDb=role.get();
            roleFromDb.setDescription(newRole.getDescription());
            roleFromDb.setName(newRole.getName());
            roleFromDb.setPrivileges(newRole.getPrivileges());
            return saveRole(roleFromDb);
        }
        return null;
    }

    public void deleteRoleById(Long id){
        roleRepository.deleteById(id);
    }
    
}

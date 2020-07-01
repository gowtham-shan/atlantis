package com.halnode.atlantis.core.service;

import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.model.Role;
import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.RoleRepository;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import com.halnode.atlantis.util.Constants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @NonNull
    private final RoleRepository roleRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        //TODO: Throw proper exception instead of null
        return null;
    }

    public Set<Role> createAdminRole() {
        Role role = roleRepository.findByName("ROLE_ADMIN");
        if (role == null) {
            role = new Role();
            role.setName("ROLE_ADMIN");
            role = roleRepository.save(role);
        }
        return new HashSet<>(Arrays.asList(role));
    }

    public User saveUser(User user) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        if (user.getIsAdmin()) {
            user.setRoles(createAdminRole());
        }
        //TODO:Have to write logic for saving users with existing roles and privileges
        return userRepository.save(user);
    }

    public void saveUsers(Set<User> users, Organization organization, boolean firstUser) {
        users.forEach(user -> {
            String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setOrganization(organization);
            if (firstUser && ObjectUtils.isEmpty(user.getRoles())) {
                user.setRoles(createAdminRole());
                user.setIsAdmin(true);
            }
            user.setActive(true);
            Constants.ORGANIZATION_SCHEMA_MAP.put(user.getMobileNumber(), user.getOrganization().getName());
        });
        userRepository.saveAll(users);
    }
}

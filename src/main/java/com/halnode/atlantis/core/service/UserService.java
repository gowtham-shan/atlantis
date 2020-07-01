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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @NonNull
    private final RoleRepository roleRepository;

    public Set<Role> createInitialRole() {
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
        return userRepository.save(user);
    }

    public void saveUsers(List<User> users, Organization organization, boolean firstUser) {
        users.forEach(user -> {
            String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            user.setOrganization(organization);
            if (firstUser && ObjectUtils.isEmpty(user.getRoles())) {
                user.setRoles(createInitialRole());
            }
            Constants.ORGANIZATION_SCHEMA_MAP.put(user.getMobileNumber(), user.getOrganization().getName());
        });
        userRepository.saveAll(users);
    }
}

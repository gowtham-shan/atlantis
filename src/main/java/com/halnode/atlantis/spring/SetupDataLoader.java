package com.halnode.atlantis.spring;

import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.model.Privilege;
import com.halnode.atlantis.core.persistence.model.Role;
import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.OrganizationRepository;
import com.halnode.atlantis.core.persistence.repository.PrivilegeRepository;
import com.halnode.atlantis.core.persistence.repository.RoleRepository;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import com.halnode.atlantis.util.Constants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${setup.insert-sample-data}")
    private boolean insertData;

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final OrganizationRepository organizationRepository;

    @NonNull
    private final PrivilegeRepository privilegeRepository;

    @NonNull
    private final RoleRepository roleRepository;

    @NonNull
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (insertData) {
            // create initial privileges
            createPrivilegeIfNotFound("READ_PRIVILEGE");
            createPrivilegeIfNotFound("WRITE_PRIVILEGE");
            // create initial roles
            createRoleIfNotFound("ROLE_ADMIN");
            // create initial user
            createUserIfNotFound();
            // create initial organization
            createOrganizationIfNotFound();
            // Map user to the organization
            MapToOrganization();
        }
        insertData = false;
        /*
            Load all organization names in the  ORGANIZATION_SCHEMA_MAP
            which will be later used to get and set the schema name while running organization specific queries
         */
        List<User> users = userRepository.findAll();
        if (!ObjectUtils.isEmpty(users)) {
            users.stream().filter(Objects::nonNull).forEach(user -> {
                if (!ObjectUtils.isEmpty(user.getOrganization())) {
                    Constants.ORGANIZATION_SCHEMA_MAP.put(user.getMobileNumber(), user.getOrganization().getName());
                }
            });
        }
    }

    @Transactional
    public void createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege();
            privilege.setName(name);
        }
        privilegeRepository.save(privilege);
    }

    @Transactional
    public void createRoleIfNotFound(final String name) {
        Role role = roleRepository.findByName(name);
        List<Privilege> privileges_ = privilegeRepository.findAll();
        if (role == null) {
            role = new Role();
            role.setName(name);
            role.setDescription("Sample Description");
        }
        roleRepository.save(role);
    }

    @Transactional
    public void createUserIfNotFound() {
        User user = userRepository.findByUserName("test");
        if (user == null) {
            user = new User();
            user.setUserName("test");
            user.setFirstName("Test");
            user.setLastName("Test");
            user.setPassword(bCryptPasswordEncoder.encode("test"));
            user.setMobileNumber(9876543210L);
            user.setEmail("test@halnode.com");
            user.setActive(true);
        }
        userRepository.save(user);
    }

    @Transactional
    public void createOrganizationIfNotFound() {
        Organization organization = organizationRepository.findByName("TEST_ORG");
        if (organization == null) {
            organization = new Organization();
            organization.setName("TEST_ORG");
            organizationRepository.save(organization);
        }
    }

    public void MapToOrganization() {
        List<Privilege> privileges = privilegeRepository.findAll();
        Role role = roleRepository.findByName("ROLE_ADMIN");
        role.setPrivileges(privileges.stream().collect(Collectors.toSet()));
        roleRepository.save(role);
        Organization organization = organizationRepository.findByName("TEST_ORG");
        User user = userRepository.findByUserName("test");
        user.setRoles(new HashSet<>(Arrays.asList(role)));
        user.setOrganization(organization);
        userRepository.save(user);
    }
}

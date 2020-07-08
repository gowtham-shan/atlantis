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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @NonNull
    private final RoleRepository roleRepository;

    @NonNull
    private final EntityManagerFactory entityManagerFactory;

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

    @Transactional
    public User saveUser(User user) {
        if (!ObjectUtils.isEmpty(user.getOrganization())) {
            String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            Query query;
            entityManager.getTransaction().begin();
            query = entityManager.createNativeQuery(String.format("SET SCHEMA \'%s\';", user.getOrganization().getName()));
            query.executeUpdate();
            Role adminRole = null;
            if (user.getIsAdmin()) {
                String sql = "SELECT ROLE.* FROM role AS ROLE WHERE role_name = :roleName";
                Query role_query = entityManager.createNativeQuery(sql, Role.class);
                role_query.setParameter("roleName", "ROLE_ADMIN");
                adminRole = (Role) role_query.getSingleResult();
            }
            user.setRoles(new HashSet<>(Arrays.asList(adminRole)));
            entityManager.persist(user);
            entityManager.getTransaction().commit();
            entityManager.close();
            Constants.ORGANIZATION_SCHEMA_MAP.put(user.getUserName(), user.getOrganization().getName());
        }
        return user;
    }

    @Transactional
    public void saveUser(User user, Organization organization, boolean firstUser) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setOrganization(organization);
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Query query;
        entityManager.getTransaction().begin();
        query = entityManager.createNativeQuery(String.format("SET SCHEMA \'%s\';", user.getOrganization().getName()));
        query.executeUpdate();
        Role role = null;
        if (firstUser) {
            String sql = "SELECT ROLE.* FROM role AS ROLE WHERE role_name = :roleName";
            Query role_query = entityManager.createNativeQuery(sql, Role.class);
            role_query.setParameter("roleName", "ROLE_ADMIN");
            role = (Role) role_query.getSingleResult();
        }
        user.setRoles(new HashSet<>(Arrays.asList(role)));
        user.setIsAdmin(true);
        user.setActive(true);
        entityManager.persist(user);
        entityManager.getTransaction().commit();
        entityManager.close();
        Constants.ORGANIZATION_SCHEMA_MAP.put(user.getUserName(), organization.getName());
    }
}

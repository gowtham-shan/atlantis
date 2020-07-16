package com.halnode.atlantis.core.service;

import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.model.Role;
import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.RoleRepository;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import com.halnode.atlantis.util.Constants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
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
    private final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
            EntityManagerFactory emf = localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory();
            EntityManager entityManager = emf.createEntityManager();
            JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManager);
            Query query;
            entityManager.getTransaction().begin();
            query = entityManager.createNativeQuery(String.format("SET SCHEMA \'%s\';", user.getOrganization().getName()));
            query.executeUpdate();

            Role adminRole = null;
            if (user.getIsAdmin()) {
                RoleRepository roleRepository = jpaRepositoryFactory.getRepository(RoleRepository.class);
                adminRole = roleRepository.findByName("ROLE_ADMIN");
//                String sql = "SELECT ROLE.* FROM role AS ROLE WHERE role_name = :roleName";
//                Query role_query = entityManager.createNativeQuery(sql, Role.class);
//                role_query.setParameter("roleName", "ROLE_ADMIN");
//                adminRole = (Role) role_query.getSingleResult();
            }
            UserRepository userRepository = jpaRepositoryFactory.getRepository(UserRepository.class);
            user.setRoles(new HashSet<>(Arrays.asList(adminRole)));
            userRepository.save(user);
//            entityManager.persist(user);
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

        EntityManagerFactory emf = localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory();
        EntityManager entityManager = emf.createEntityManager();
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManager);
        Query query;
        entityManager.getTransaction().begin();
        query = entityManager.createNativeQuery(String.format("SET SCHEMA \'%s\';", user.getOrganization().getName()));
        query.executeUpdate();
        Role role = null;

        if (firstUser) {
            RoleRepository roleRepository = jpaRepositoryFactory.getRepository(RoleRepository.class);
            role = roleRepository.findByName("ROLE_ADMIN");
//            String sql = "SELECT ROLE.* FROM role AS ROLE WHERE role_name = :roleName";
//            Query role_query = entityManager.createNativeQuery(sql, Role.class);
//            role_query.setParameter("roleName", "ROLE_ADMIN");
//            role = (Role) role_query.getSingleResult();
        }
        user.setRoles(new HashSet<>(Arrays.asList(role)));
        user.setIsAdmin(true);
        user.setActive(true);
        UserRepository userRepository = jpaRepositoryFactory.getRepository(UserRepository.class);
        userRepository.save(user);
        entityManager.getTransaction().commit();
        entityManager.close();
        Constants.ORGANIZATION_SCHEMA_MAP.put(user.getUserName(), organization.getName());
    }
}

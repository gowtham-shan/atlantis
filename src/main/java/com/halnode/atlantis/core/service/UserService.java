package com.halnode.atlantis.core.service;

import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.model.Role;
import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.RoleRepository;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
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

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    @Transactional
    public User saveUser(User user) {
        if (!ObjectUtils.isEmpty(user.getOrganization())) {
            return this.saveUser(user, user.getOrganization(), user.getIsAdmin());
        }
        return null;
    }

    @Transactional
    public User saveUser(User user, Organization organization, boolean isAdmin) {
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
        if (isAdmin) {
            RoleRepository roleRepository = jpaRepositoryFactory.getRepository(RoleRepository.class);
            role = roleRepository.findByName("ROLE_ADMIN");
        }
        user.setRoles(new HashSet<>(Arrays.asList(role)));
        user.setIsAdmin(isAdmin);
        user.setActive(true);
        UserRepository userRepository = jpaRepositoryFactory.getRepository(UserRepository.class);
        User savedUser = userRepository.save(user);
        entityManager.getTransaction().commit();
        entityManager.close();
        return savedUser;
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

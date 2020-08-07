package com.halnode.atlantis.core.service;

import com.halnode.atlantis.core.constants.UserType;
import com.halnode.atlantis.core.exception.UserNameAlreadyExistsException;
import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.model.Role;
import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.RoleRepository;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
            return this.saveUser(user, user.getOrganization(), user.getUserType());
        }
        return null;
    }

    public EntityManager getEntityManager(String schemaName){
        EntityManagerFactory emf = localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory();
        EntityManager entityManager = emf.createEntityManager();
        Query query;
        entityManager.getTransaction().begin();
        query = entityManager.createNativeQuery(String.format("SET SCHEMA \'%s\';", schemaName));
        query.executeUpdate();
        return entityManager;
    }

    @Transactional
    public User saveUser(User user, Organization organization, UserType userType) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setOrganization(organization);
        Role role;
        EntityManager entityManager=this.getEntityManager(user.getOrganization().getName());
        JpaRepositoryFactory jpaRepositoryFactory =new JpaRepositoryFactory(entityManager);

        RoleRepository roleRepository = jpaRepositoryFactory.getRepository(RoleRepository.class);
        if (UserType.ADMIN.equals(userType)) {
            role = roleRepository.findByName("ROLE_ADMIN");
        }else if(UserType.STAFF.equals(userType)){
            role=roleRepository.findByName("ROLE_STAFF");
        }else if(UserType.CUSTOMER.equals(userType)){
            role=roleRepository.findByName("ROLE_CUSTOMER");
        }else{
            String type="ROLE_"+userType.toString();
            role=roleRepository.findByName(type);
        }
        user.setRoles(new HashSet<>(Arrays.asList(role)));
        user.setUserType(userType);
        user.setActive(true);
        UserRepository userRepository = jpaRepositoryFactory.getRepository(UserRepository.class);
        User savedUser = userRepository.save(user);
        entityManager.getTransaction().commit();
        entityManager.close();
        return savedUser;
    }


    public User updateUser(User newUser){
        EntityManager entityManager = this.getEntityManager(newUser.getOrganization().getName());
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManager);
        UserRepository userRepository = jpaRepositoryFactory.getRepository(UserRepository.class);
        Optional<User> optionalUser=userRepository.findById(newUser.getId());
        if(optionalUser.isPresent()){
            try{
                User userFromDb=optionalUser.get();
                if(!bCryptPasswordEncoder.encode(newUser.getPassword()).equals(userFromDb.getPassword())){
                    userFromDb.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
                }
                userFromDb.setActive(newUser.isActive());
                userFromDb.setUserName(newUser.getUserName());
                userFromDb.setRoles(newUser.getRoles());
                userFromDb.setMobileNumber(newUser.getMobileNumber());
                userFromDb.setEmail(newUser.getEmail());
                userFromDb.setName(newUser.getName());
                User updatedUser=userRepository.save(userFromDb);
                entityManager.getTransaction().commit();
                entityManager.close();
                return updatedUser;
            }catch (Exception e){
                throw new UserNameAlreadyExistsException("User with a user name already exists");
            }
        }
        throw new UsernameNotFoundException("User is not found");
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

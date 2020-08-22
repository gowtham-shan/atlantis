package com.halnode.atlantis.core.service;

import com.halnode.atlantis.core.constants.UserType;
import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.model.Role;
import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.RoleRepository;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
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
        return userOptional.orElse(null);
    }

    @Transactional
    public User saveUser(User user) {
        if (!ObjectUtils.isEmpty(user.getOrganization())) {
            return this.saveUser(user, user.getOrganization(), user.getUserType());
        }
        return null;
    }

    @Transactional
    public User saveUser(User user, Organization organization, UserType userType) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setOrganization(organization);
        Role role;
        EntityManagerFactory emf = localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory();
        EntityManager entityManager = emf.createEntityManager();
        Query query;
        entityManager.getTransaction().begin();
        query = entityManager.createNativeQuery(String.format("SET SCHEMA '%s';", user.getOrganization().getName()));
        query.executeUpdate();

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
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        user.setUserType(userType);
        user.setActive(true);
        UserRepository userRepository = jpaRepositoryFactory.getRepository(UserRepository.class);
        User savedUser = userRepository.save(user);
        entityManager.getTransaction().commit();
        entityManager.close();
        return savedUser;
    }


    public User updateUser(User newUser){
        Optional<User> optionalUser=userRepository.findById(newUser.getId());
        if(optionalUser.isPresent()){
            try{
                User userFromDb=optionalUser.get();
                if(!bCryptPasswordEncoder.encode(newUser.getPassword()).equals(userFromDb.getPassword())){
                    userFromDb.setPassword(bCryptPasswordEncoder.encode(newUser.getPassword()));
                }
                userFromDb.setUserName(newUser.getUserName());
                userFromDb.setActive(newUser.isActive());
                userFromDb.setRoles(newUser.getRoles());
                userFromDb.setMobileNumber(newUser.getMobileNumber());
                userFromDb.setEmail(newUser.getEmail());
                userFromDb.setName(newUser.getName());
                return userRepository.save(userFromDb);
            }catch (Exception e){
                log.error("Exception while updating the user : "+e.getMessage());
                return null;
            }
        }
        throw new UsernameNotFoundException("User is not found");
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}

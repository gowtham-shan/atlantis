package com.halnode.atlantis.spring.authentication;

import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @NonNull
    private final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    /**
     * This method will be called by spring security and hibernate in order to authenticate/authorize user and
     * set the schema for the current logged in/anonymous user respectively.
     *
     * @param <<code>userIdentifier</code> -> must be suffixed with @{organization name}
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userIdentifier) throws UsernameNotFoundException {
        /*
            Since we use suffixed userIdentifier which is different from the actual user name(i.e., user@organization),
            This if block will prevent IndexOutOfBoundsException when REMEMBER ME option is enabled.
         */
        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return userDetails;
        }

        User user = null;

        //Separate user_name and org_name(i.e., SCHEMA NAME) from the input

        String currentUserName = userIdentifier.substring(0, userIdentifier.lastIndexOf("@"));
        String currentOrgName = userIdentifier.substring(userIdentifier.lastIndexOf("@") + 1, userIdentifier.length());

        //Setup the current schema
        EntityManagerFactory emf = localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory();
        EntityManager entityManager = emf.createEntityManager();
        Query query;
        entityManager.getTransaction().begin();
        query = entityManager.createNativeQuery(String.format("SET SCHEMA \'%s\';", currentOrgName));
        query.executeUpdate();

        //Get the user from the db
        JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManager);
        UserRepository userRepository = jpaRepositoryFactory.getRepository(UserRepository.class);
        user = userRepository.findByUserName(currentUserName);
        entityManager.getTransaction().commit();
        entityManager.close();

        if (ObjectUtils.isEmpty(user)) throw new UsernameNotFoundException(currentUserName);

        //Set roles from the user objects
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        if (!ObjectUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
            });
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), grantedAuthorities);
    }
}

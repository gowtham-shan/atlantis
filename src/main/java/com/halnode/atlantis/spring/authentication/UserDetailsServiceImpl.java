package com.halnode.atlantis.spring.authentication;

import com.halnode.atlantis.core.persistence.model.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    private final EntityManagerFactory entityManagerFactory;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userIdentifier) throws UsernameNotFoundException {
        User user = null;

        //Separate user_name and org_name(i.e., SCHEMA NAME) from the input
        String currentUserName = userIdentifier.substring(0, userIdentifier.lastIndexOf("@"));
        String currentOrgName = userIdentifier.substring(userIdentifier.lastIndexOf("@") + 1, userIdentifier.length());
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Query query;
        entityManager.getTransaction().begin();
        query = entityManager.createNativeQuery(String.format("SET SCHEMA \'%s\';", currentOrgName));
        query.executeUpdate();

        //Get the user from the database for the given user_name
        String sql = "SELECT u FROM User u WHERE u.userName=:userName";
        Query user_query = entityManager.createQuery(sql);
        user_query.setParameter("userName", currentUserName);
        user = (User) user_query.getSingleResult();
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

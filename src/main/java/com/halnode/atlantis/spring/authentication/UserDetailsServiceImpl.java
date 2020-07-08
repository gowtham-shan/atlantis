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
import java.util.List;
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
        String currentUserName = userIdentifier.substring(0, userIdentifier.lastIndexOf("@"));
        String currentOrgName = userIdentifier.substring(userIdentifier.lastIndexOf("@") + 1, userIdentifier.length());
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        Query query;
        entityManager.getTransaction().begin();
        query = entityManager.createNativeQuery(String.format("SET SCHEMA \'%s\';", currentOrgName));
        query.executeUpdate();
        String sql = "SELECT u FROM User u";
        Query user_query = entityManager.createQuery(sql);
        List<User> users = user_query.getResultList();
        User user1 = null;
        for (User u : users) {
            if (u.getUserName().equals(currentUserName)) {
                user1 = u;
            }
        }
        entityManager.getTransaction().commit();
        entityManager.close();
        if (ObjectUtils.isEmpty(user1)) throw new UsernameNotFoundException(currentUserName);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        if (user1 != null) {
            user1.getRoles().forEach(role -> {
                System.out.println("Matched User Role : " + role.getName());
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
            });
        }

        return new org.springframework.security.core.userdetails.User(user1.getUserName(), user1.getPassword(), grantedAuthorities);
    }
}

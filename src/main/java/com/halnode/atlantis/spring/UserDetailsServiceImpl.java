package com.halnode.atlantis.spring;

import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
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
    private final UserRepository userRepository;

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
        String sql = "SELECT u.* FROM auth_user u WHERE u.user_name=:userName";
        Query user_query = entityManager.createNativeQuery(sql, User.class);
        user_query.setParameter("userName", currentUserName);
        user = (User) user_query.getSingleResult();
        entityManager.getTransaction().commit();
        if (ObjectUtils.isEmpty(user)) throw new UsernameNotFoundException(currentUserName);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        });

        return new org.springframework.security.core.userdetails.User(String.valueOf(user.getMobileNumber()), user.getPassword(), grantedAuthorities);
    }
}

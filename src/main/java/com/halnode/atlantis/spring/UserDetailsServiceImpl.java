package com.halnode.atlantis.spring;

import com.halnode.atlantis.core.persistence.model.Privilege;
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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @NonNull
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUserName(username);
        if (ObjectUtils.isEmpty(user)) throw new UsernameNotFoundException(username);

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        Set<Privilege> privileges = new HashSet<>();
        // Get privileges for the particular user from user's group
        user.getGroups().stream().filter(Objects::nonNull).forEach(group -> {
            group.getRoles().stream().filter(Objects::nonNull).forEach(role -> {
                privileges.addAll(role.getPrivileges());
            });
        });
        // Add the above fetched privileges to the granted authorities
        privileges.stream().filter(Objects::nonNull).forEach(privilege -> {
            grantedAuthorities.add(new SimpleGrantedAuthority(privilege.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), grantedAuthorities);
    }
}

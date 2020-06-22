package com.halnode.atlantis.spring;


import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.security.Principal;
import java.util.Optional;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    static final String DEFAULT_TENANT = "public";

    @NonNull
    private final UserRepository userRepository;

    @Override
    public String resolveCurrentTenantIdentifier() {
        Authentication auth= SecurityContextHolder.getContext().getAuthentication();
       if(!ObjectUtils.isEmpty(auth)) {
           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
           if (!(authentication instanceof AnonymousAuthenticationToken)) {
               String currentUserName = authentication.getName();
               User auth_user=userRepository.findByUserName(currentUserName);
               return auth_user.getOrganization().getName();
           }
//        if(!ObjectUtils.isEmpty(auth)){
//            UserDetails user = (UserDetails)auth.getPrincipal();
//            if(!ObjectUtils.isEmpty(user)){
//                String userName=user.getUsername();
//                User auth_user=userRepository.findByUserName(userName);
//                return auth_user.getOrganization().getName();
//            }
//            return DEFAULT_TENANT;
//        }

       }
        return DEFAULT_TENANT;

//        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
//                .filter(Predicate.not(authentication -> authentication instanceof AnonymousAuthenticationToken))
//                .map(Principal::getName)
//                .orElse(DEFAULT_TENANT);
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }
}

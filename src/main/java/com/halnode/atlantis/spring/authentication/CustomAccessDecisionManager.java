package com.halnode.atlantis.spring.authentication;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Implementation for {@link AccessDecisionManager}.
 * It decides whether the requested url is accessible or not
 * for the authenticated user based on their granted authorities/roles
 *
 * @author gowtham
 */
@Log4j2
public class CustomAccessDecisionManager implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        /*
            ConfigAttributes will be empty(if not mentioned as null in url_configuration file)
            or null (if specified as null in url_configuration file) when the url should be accessible by all(i.e, permitAll).
        */
        if (ObjectUtils.isEmpty(configAttributes)) {
            return;
        }
        Iterator<ConfigAttribute> iterator = configAttributes.iterator();
        while (iterator.hasNext()) {
            String neededRole = iterator.next().getAttribute();
            if (!StringUtils.isEmpty(neededRole)) {
                for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
                    if (neededRole.trim().equalsIgnoreCase(grantedAuthority.getAuthority().trim())) {
                        return;
                    }
                }
            }
        }
        throw new AccessDeniedException("Access is denied");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}

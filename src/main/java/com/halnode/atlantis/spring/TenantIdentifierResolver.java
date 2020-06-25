package com.halnode.atlantis.spring;


import lombok.RequiredArgsConstructor;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
@RequiredArgsConstructor
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    static final String DEFAULT_TENANT = "public";

    @Override
    public String resolveCurrentTenantIdentifier() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!ObjectUtils.isEmpty(auth)) {
            if (!(auth instanceof AnonymousAuthenticationToken)) {
                String currentUserName = auth.getName();
                return SetupDataLoader.organizationSchemaMap.get(currentUserName);
            }
            return DEFAULT_TENANT;
        }

        return DEFAULT_TENANT;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}

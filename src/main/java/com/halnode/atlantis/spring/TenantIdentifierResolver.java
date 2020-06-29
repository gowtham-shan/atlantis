package com.halnode.atlantis.spring;


import com.halnode.atlantis.util.Constants;
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

    @Override
    public String resolveCurrentTenantIdentifier() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!ObjectUtils.isEmpty(auth)) {
            if (!(auth instanceof AnonymousAuthenticationToken)) {
                String currentUserName = auth.getName();
                return Constants.ORGANIZATION_SCHEMA_MAP.get(Long.valueOf(currentUserName));
            }
            return Constants.DEFAULT_TENANT;
        }

        return Constants.DEFAULT_TENANT;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}

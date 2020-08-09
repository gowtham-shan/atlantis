package com.halnode.atlantis.spring.hibernate.tenant;


import com.halnode.atlantis.core.constants.CustomUserDetails;
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
                CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
                return userDetails.getUser().getOrganization().getName();
            }
            return Constants.DEFAULT_TENANT;
        }

        return Constants.DEFAULT_TENANT;
    }

    /**
     * Need not to check the existing db connections with the tenants.
     *
     * @return false
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}

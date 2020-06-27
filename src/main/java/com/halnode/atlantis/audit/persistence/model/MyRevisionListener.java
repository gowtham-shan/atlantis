package com.halnode.atlantis.audit.persistence.model;

import com.halnode.atlantis.util.Constants;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.ObjectUtils;

public class MyRevisionListener implements RevisionListener {
    
    @Override
    public void newRevision(Object revisionEntity) {
        CustomRevisionEntity revision = (CustomRevisionEntity) revisionEntity;
        revision.setUserName(this.getUserName());
    }

    private String getUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!ObjectUtils.isEmpty(authentication) && !(authentication instanceof AnonymousAuthenticationToken)) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        return Constants.DEFAULT_USER;
    }
}

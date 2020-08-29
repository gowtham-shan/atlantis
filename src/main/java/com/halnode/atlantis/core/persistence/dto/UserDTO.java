package com.halnode.atlantis.core.persistence.dto;

import com.halnode.atlantis.core.constants.UserType;
import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.model.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

    private boolean isAuthenticated;
    private User user;
    private Organization organization;
    private UserType type;

}

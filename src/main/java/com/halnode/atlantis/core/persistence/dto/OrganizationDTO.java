package com.halnode.atlantis.core.persistence.dto;

import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class OrganizationDTO {
    private Organization organization;
    private List<User> usersList;
}

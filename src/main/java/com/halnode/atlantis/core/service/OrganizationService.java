package com.halnode.atlantis.core.service;

import com.halnode.atlantis.core.persistence.dto.OrganizationDTO;
import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.OrganizationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    @NonNull
    private final DataSource dataSource;

    @NonNull
    private final OrganizationRepository organizationRepository;

    @NonNull
    private final UserService userService;

    public List<Organization> getOrganizations() {
        return organizationRepository.findAll();
    }

    /**
     * Apply migration scripts for newly created organization
     *
     * @param schema
     */
    public void initDatabase(String schema) {
        Flyway flyway = Flyway.configure()
                .locations("db/migration/organizations")
                .dataSource(dataSource)
                .schemas(schema)
                .load();
        flyway.migrate();
    }

    public Organization saveOrganization(OrganizationDTO organizationDTO) {
        Organization created = organizationRepository.save(organizationDTO.getOrganization());
        Set<User> usersList = organizationDTO.getUsers();
        if (!ObjectUtils.isEmpty(usersList)) {
            userService.saveUsers(usersList, created, true);
        }
        this.initDatabase(created.getName());
        return created;
    }
}
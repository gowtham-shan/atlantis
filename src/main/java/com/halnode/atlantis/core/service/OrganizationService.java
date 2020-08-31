package com.halnode.atlantis.core.service;

import com.halnode.atlantis.core.constants.UserType;
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
     * Apply migration scripts for newly created organization.
     * Must be called for every newly created organization
     *
     * @param schema schema name
     */
    public void initDatabaseSchema(String schema) {
        Flyway flyway = Flyway.configure()
                .locations("db/migration/organizations")
                .dataSource(dataSource)
                .schemas(schema)
                .load();
        flyway.migrate();
    }

    public Organization saveOrganization(Organization organization) {
        return organizationRepository.save(organization);
    }

    public Organization saveOrganization(OrganizationDTO organizationDTO) {
        Organization created = organizationRepository.save(organizationDTO.getOrganization());
        try {
            this.initDatabaseSchema(created.getName());
            User user = organizationDTO.getUser();
            if (!ObjectUtils.isEmpty(user)) {
                userService.saveUser(user, created, UserType.ADMIN);
            }
        } catch (Exception e) {

        }

        return created;
    }

    public Organization updateOrganization(Organization organization) {
        return organizationRepository.save(organization);
    }

    public void deleteOrganization(Long id) {
        organizationRepository.deleteById(id);
    }
}
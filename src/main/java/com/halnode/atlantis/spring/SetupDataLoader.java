package com.halnode.atlantis.spring;

import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.model.TestEntity;
import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.OrganizationRepository;
import com.halnode.atlantis.core.persistence.repository.TestEntityRepository;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import com.halnode.atlantis.util.Constants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    @NonNull
    private final UserRepository userRepository;

    @NonNull
    private final OrganizationRepository organizationRepository;

    @NonNull
    private final TestEntityRepository testEntityRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        /*
            Load all organization names in the  ORGANIZATION_SCHEMA_MAP
            which will be later used to get and set the schema name while running organization specific queries
         */
        List<User> users = userRepository.findAll();
        users.stream().filter(Objects::nonNull).forEach(user -> {
            Constants.ORGANIZATION_SCHEMA_MAP.put(user.getMobileNumber(), user.getOrganization().getName());
        });

        Organization organization = new Organization();
        organization.setName("ORG_ONE");
        testEntityRepository.deleteAll();
        organizationRepository.deleteAll();
        organizationRepository.save(organization);
        TestEntity testEntity = new TestEntity();
        testEntity.setName("One");
        testEntity.setOrganization(organization);
        testEntityRepository.save(testEntity);
    }
}

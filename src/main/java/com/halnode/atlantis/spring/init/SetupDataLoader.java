package com.halnode.atlantis.spring.init;

import com.halnode.atlantis.core.persistence.model.Organization;
import com.halnode.atlantis.core.persistence.model.User;
import com.halnode.atlantis.core.persistence.repository.OrganizationRepository;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import com.halnode.atlantis.util.Constants;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    @NonNull
    private final OrganizationRepository organizationRepository;

    @NonNull
    private final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        /*
            Load all organization names in the  ORGANIZATION_SCHEMA_MAP
            which will be later used to get and set the schema name while running organization specific queries
         */
        List<Organization> organizationList = organizationRepository.findAll();
        organizationList.forEach(organization -> {

            EntityManagerFactory emf = localContainerEntityManagerFactoryBean.getNativeEntityManagerFactory();
            EntityManager entityManager = emf.createEntityManager();
            Query query;
            entityManager.getTransaction().begin();
            query = entityManager.createNativeQuery(String.format("SET SCHEMA \'%s\';", organization.getName()));
            query.executeUpdate();

            JpaRepositoryFactory jpaRepositoryFactory = new JpaRepositoryFactory(entityManager);
            UserRepository userRepository = jpaRepositoryFactory.getRepository(UserRepository.class);
            List<User> userList = userRepository.findUsersByOrOrganizationId(organization.getId());

            userList.forEach(user -> {
                Constants.ORGANIZATION_SCHEMA_MAP.put(user.getUserName(), organization.getName());
            });
        });
    }
}

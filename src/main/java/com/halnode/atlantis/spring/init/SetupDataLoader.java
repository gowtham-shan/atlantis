package com.halnode.atlantis.spring.init;

import com.halnode.atlantis.core.persistence.repository.OrganizationRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    @NonNull
    private final OrganizationRepository organizationRepository;

    @NonNull
    private final LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
       
    }
}

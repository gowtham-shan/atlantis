package com.halnode.atlantis.spring.flyway;

import com.halnode.atlantis.core.persistence.repository.OrganizationRepository;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuration class for migrating sql schemas using flyway during application startup.
 *
 * @author Gowtham
 */

@Configuration
public class FlywayConfig {

    @Bean
    CommandLineRunner commandLineRunner(OrganizationRepository repository, DataSource dataSource) {
        return args -> repository.findAll().forEach(organization -> {
            String schema = organization.getName();
            Flyway flyway = Flyway.configure()
                    .locations("/db/migration/organizations")
                    .dataSource(dataSource)
                    .schemas(schema)
                    .load();
            flyway.migrate();
        });
    }

}

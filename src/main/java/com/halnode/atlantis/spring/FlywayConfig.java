package com.halnode.atlantis.spring;

import com.halnode.atlantis.core.persistence.repository.OrganizationRepository;
import com.halnode.atlantis.core.persistence.repository.UserRepository;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {

    @Bean
    public Flyway flyway(DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .locations("db/migration/public")
                .dataSource(dataSource)
                .schemas(TenantIdentifierResolver.DEFAULT_TENANT)
                .load();
//        flyway.clean();
//        flyway.repair();
//        flyway.baseline();
        flyway.migrate();

        return flyway;
    }

    @Bean
    CommandLineRunner commandLineRunner(OrganizationRepository repository, DataSource dataSource) {
        return args -> {
            repository.findAll().forEach(organization -> {
                String schema=organization.getName();
                Flyway flyway = Flyway.configure()
                        .locations("/db/migration/organizations")
                        .dataSource(dataSource)
                        .schemas(schema)
                        .load();
                flyway.migrate();
            });
        };
//        return args -> {
//            repository.findAll().forEach(user -> {
//                String tenant = user.getUserName();
//                Flyway flyway = Flyway.configure()
//                        .locations("/db/migration/organizations")
//                        .dataSource(dataSource)
//                        .schemas(tenant)
//                        .load();
//                flyway.migrate();
//            });
//        };
    }
}

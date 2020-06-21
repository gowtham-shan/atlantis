package com.halnode.atlantis.core.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@RequiredArgsConstructor
public class TenantService {

    @NonNull
    private final DataSource dataSource;

    public void initDatabase(String schema) {
        Flyway flyway = Flyway.configure()
                .locations("db/migration/organizations")
                .dataSource(dataSource)
                .schemas(schema)
                .load();
        flyway.migrate();
    }
}
package com.halnode.atlantis.spring;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class ConnectionProviderImpl implements ConnectionProvider {

    private DataSource dataSource;

    private String tenantIdentifier;

    public ConnectionProviderImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setTenantIdentifier(String tenantIdentifier) {
        this.tenantIdentifier = tenantIdentifier;
    }

    @Override
    public Connection getConnection() throws SQLException {
        final Connection connection = dataSource.getConnection();
        connection.createStatement()
                .execute(String.format("SET SCHEMA \'%s\';", "public"));
        return connection;
    }

    @Override
    public void closeConnection(Connection conn) throws SQLException {
        conn.createStatement()
                .execute(String.format("SET SCHEMA \'%s\';", tenantIdentifier));
        conn.close();
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        return null;
    }
}

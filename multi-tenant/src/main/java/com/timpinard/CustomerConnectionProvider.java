package com.timpinard;

import org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl;
import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class is responsible for providing the database connection for a multi-tenant application.
 * It extends the AbstractMultiTenantConnectionProvider class and overrides the necessary methods.
 */
public class CustomerConnectionProvider extends AbstractMultiTenantConnectionProvider<String> {

    /**
     * This variable represents the database connection provider for a multi-tenant application.
     * The connection provider is responsible for providing the necessary connections to the database for each tenant.
     * It is an instance of the ConnectionProvider interface, an implementation of which is used by the CustomerConnectionProvider class.
     * The initialization of the connection provider is done in the initConnectionProvider() method of the CustomerConnectionProvider class.
     * The connection provider is configured using the properties specified in the "hibernate.properties" file.
     *
     * @see ConnectionProvider
     * @see CustomerConnectionProvider#initConnectionProvider()
     */
    private final ConnectionProvider connectionProvider;

    public CustomerConnectionProvider() throws IOException {
        connectionProvider = initConnectionProvider();
    }

    @Override
    protected ConnectionProvider getAnyConnectionProvider() {
        return connectionProvider;
    }

    @Override
    protected ConnectionProvider selectConnectionProvider(String tenantIdentifier) {
        return connectionProvider;
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection connection = super.getConnection(tenantIdentifier);
        try (Statement statement = connection.createStatement()) {
            statement.execute(String.format("use %s;", tenantIdentifier));
        }
        return connection;
    }

    /**
     * This method initializes and configures the connection provider for the multi-tenant application.
     * The connection provider is responsible for providing the necessary connections to the database for each tenant.
     *
     * @return The initialized connection provider.
     * @throws IOException If an I/O error occurs while loading the properties file.
     */
    private org.hibernate.engine.jdbc.connections.spi.ConnectionProvider initConnectionProvider() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/hibernate.properties"));
        Map<String, Object> configProperties = new HashMap<>();
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            configProperties.put(key, value);
        }

        DriverManagerConnectionProviderImpl connectionProvider = new DriverManagerConnectionProviderImpl();
        connectionProvider.configure(configProperties);
        return connectionProvider;
    }

}

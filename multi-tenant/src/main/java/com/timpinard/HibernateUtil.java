package com.timpinard;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * Utility class for managing Hibernate session factory and sessions.
 */
public class HibernateUtil {

    /**
     * The volatile variable that holds an instance of the Hibernate SessionFactory class.
     * The SessionFactory class is responsible for creating and managing session objects for interacting with the database.
     *
     * This variable is eagerly initialized and follows the double-checked locking idiom to ensure thread-safety and lazy initialization.
     */
    private static volatile SessionFactory sessionFactory;

    static {
        try {
            sessionFactory = getSessionFactory();
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Retrieves the Hibernate SessionFactory instance.
     *
     * @throws IOException if an I/O error occurs while loading properties.
     * @return the SessionFactory instance.
     */
    public static SessionFactory getSessionFactory() throws IOException {
        if (sessionFactory == null) {
            synchronized (HibernateUtil.class) {
                if (sessionFactory == null) {
                    ServiceRegistry serviceRegistry = configureServiceRegistry();
                    sessionFactory = makeSessionFactory(serviceRegistry);
                }
            }
        }
        return sessionFactory;
    }

    /**
     * Creates a Hibernate SessionFactory instance using the provided ServiceRegistry.
     *
     * @param serviceRegistry the ServiceRegistry for configuring Hibernate
     * @return the created SessionFactory instance
     */
    private static SessionFactory makeSessionFactory(
            ServiceRegistry serviceRegistry) {
        MetadataSources metadataSources = new MetadataSources(serviceRegistry);
        metadataSources.addPackage("com.timpinard");
        metadataSources.addAnnotatedClass(Customer.class);
        metadataSources.addAnnotatedClass(Revenue.class);
        Metadata metadata = metadataSources.getMetadataBuilder()
                .build();
        return metadata.getSessionFactoryBuilder()
                .build();

    }

    /**
     * Configures the ServiceRegistry for the Hibernate session factory.
     *
     * @throws IOException if an I/O error occurs while loading properties.
     * @return the configured ServiceRegistry.
     */
    private static ServiceRegistry configureServiceRegistry()
            throws IOException {
        Properties properties = getProperties();
        return new StandardServiceRegistryBuilder().applySettings(properties)
                .build();
    }

    /**
     * Retrieves the properties from the "hibernate.properties" file.
     * It loads the properties file using the ClassLoader and returns the Properties object.
     *
     * @throws IOException if an I/O error occurs while loading the properties file.
     * @return the loaded Properties object.
     */
    private static Properties getProperties() throws IOException {
        Properties properties = new Properties();
        URL propertiesURL = Thread.currentThread()
                .getContextClassLoader()
                .getResource("hibernate.properties");
        try (FileInputStream inputStream = new FileInputStream(
                propertiesURL.getFile())) {
            properties.load(inputStream);
        }
        return properties;
    }


    /**
     * Executes a given function within a Hibernate session.
     *
     * @param tenant   the tenant identifier to use for the session
     * @param function the function to execute within the session
     * @throws Throwable if an error occurs during execution or a rollback is performed
     */
    public void executeWithinSession(String tenant, Consumer<Session> function) {
        Session session = null;
        Transaction txn = null;
        try {
            session = sessionFactory
                    .withOptions()
                    .tenantIdentifier(tenant)
                    .openSession();
            txn = session.getTransaction();
            txn.begin();
            function.accept(session);
            txn.commit();
        } catch (Exception e) {
            if (txn != null) txn.rollback();
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

}
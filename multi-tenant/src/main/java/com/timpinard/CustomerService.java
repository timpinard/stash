package com.timpinard;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.concurrent.atomic.AtomicReference;

/**
 * This class provides methods for managing customers and revenues using Hibernate as the persistence framework.
 */
public class CustomerService {

    /**
     * Provides methods for managing the Hibernate session factory and sessions.
     */
    private HibernateUtil hibernateUtil =  new HibernateUtil();

    /**
     * Creates a new customer in the database.
     *
     * @param customer the customer object to be created
     * @return the created customer object
     */
    public Customer createCustomer(Customer customer) {
        AtomicReference<Customer> result = new AtomicReference<>();
        hibernateUtil.executeWithinSession("customer", (session) -> {
            int id = (int) session.save(customer);
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Customer> query = builder.createQuery(Customer.class);
            Root<Customer> root = query.from(Customer.class);
            query.select(root).where(builder.equal(root.get("id"), id));
            Customer existingCustomer = session.createQuery(query).uniqueResult();
            result.set(existingCustomer);
        });
        return result.get();
    }


    /**
     * Creates a revenue object and saves it in the database.
     *
     * @param revenue the revenue object to be created
     * @param tenant the tenant identifier to use for the session
     * @return the created revenue object
     */
    public Revenue createRevenue(Revenue revenue, String tenant) {
        AtomicReference<Revenue> result = new AtomicReference<>();
        hibernateUtil.executeWithinSession(tenant, (session) -> {
            int id = (int) session.save(revenue);
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Revenue> query = builder.createQuery(Revenue.class);
            Root<Revenue> root = query.from(Revenue.class);
            query.select(root).where(builder.equal(root.get("id"), id));
            Revenue existingCustomer = session.createQuery(query).uniqueResult();
            result.set(existingCustomer);
        });
        return result.get();
    }
}
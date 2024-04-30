package com.timpinard;

import java.math.BigDecimal;

public class CustomerApp {

    public static void main(String[] args) {
        createCustomer();  // Primary schema
        createRevenue();   // A specific customer tenancy
    }

    public static void createCustomer() {
        CustomerService service = new CustomerService();
        Customer customer = new Customer();
        customer.setName("customer1");
        customer.setStreet("Test Street");
        customer.setCity("Test City");
        customer.setZipCode("1234");
        customer.setTotalRevenue(BigDecimal.valueOf(500.9));

        service.createCustomer(customer);
    }

    public static void createRevenue() {
        CustomerService service = new CustomerService();
        Revenue revenue = new Revenue();
        revenue.setItem("annual subscription");
        revenue.setAmount(500.9);

        service.createRevenue(revenue, "customer1");
    }
}

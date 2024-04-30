package com.timpinard;

import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private HibernateUtil hibernateUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test createCustomer")
    void testCreateCustomer() {
        // Given
        Customer customer = new Customer();
        customer.setId(1);
        customer.setName("John Doe");
        customer.setStreet("Test Street");
        customer.setCity("Test City");
        customer.setZipCode("1234");
        customer.setTotalRevenue("1000");

        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            java.util.function.Consumer<Session> consumer = (java.util.function.Consumer<Session>) args[1];
            Session session = mock(Session.class);
            when(session.save(any(Customer.class))).thenReturn(customer);
            consumer.accept(session);
            return null;
        }).when(hibernateUtil).executeWithinSession(eq("customer"), any());

        // When
        Customer result = customerService.createCustomer(customer);

        // Then
        assertNotNull(result, "Customer should not be null");
        assertEquals(customer, result, "Returned customer should match the created one");
    }
}
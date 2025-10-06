package com.algaworks.algashop.ordering.aplication.customer.management;

import com.algaworks.algashop.ordering.aplication.customer.loyaltypoints.CustomerLoyaltyPointsApplicationService;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CantAddLoyaltyPointsOrderIsNotReady;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerArchivedException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotBelongsToCustomerException;
import com.algaworks.algashop.ordering.domain.model.order.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.listener.customer.CustomerEventLister;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class CustomerLoyaltyPointsApplicationServiceIT {

    @Autowired
    private CustomerLoyaltyPointsApplicationService service;
    
    @Autowired
    private CustomerManagementApplicationService customerService;
    
    @Autowired
    private Customers customers;
    
    @Autowired
    private Orders orders;
    
    @MockitoBean
    private CustomerEventLister customerEventLister;
    
    @Test
    public void shouldAddLoyaltyPoints() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.READY).build();
        orders.add(order);
        
        service.addLoyaltyPoints(customer.id().value(), order.id().toString());
        
        customer = customers.ofId(customer.id()).orElseThrow();

        Assertions.assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(30));
    }
    
    @Test
    public void givenNonExistingCustomer_WhenAddingLoyaltyPoints_ShouldThrowException() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.READY).build();
        
        Assertions.assertThatThrownBy(() -> service.addLoyaltyPoints(java.util.UUID.randomUUID(), order.id().toString()))
                .isInstanceOf(CustomerNotFoundException.class);
    }
    
    @Test
    public void givenArchivedCustomer_WhenAddingLoyaltyPoints_ShouldThrowException() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.READY).build();
        orders.add(order);
        
        customerService.archive(customer.id().value());
        
        Customer archivedCustomer = customers.ofId(customer.id()).orElseThrow();
        
        Assertions.assertThatThrownBy(() -> service.addLoyaltyPoints(archivedCustomer.id().value(), order.id().toString()))
                .isInstanceOf(CustomerArchivedException.class);
    }
    
    @Test
    public void givenOrderFromAnotherCustomer_WhenAddingLoyaltyPoints_ShouldThrowException() {
        Customer customer1 = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer1);
        
        Customer customer2 = CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).build();
        customers.add(customer2);

        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.READY).build();
        orders.add(order);
        
        Assertions.assertThatThrownBy(() -> service.addLoyaltyPoints(customer2.id().value(), order.id().toString()))
                .isInstanceOf(OrderNotBelongsToCustomerException.class);
    }
    
    @Test
    public void givenOrderNotReady_WhenAddingLoyaltyPoints_ShouldThrowException() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        orders.add(order);
        
        Assertions.assertThatThrownBy(() -> service.addLoyaltyPoints(customer.id().value(), order.id().toString()))
                .isInstanceOf(CantAddLoyaltyPointsOrderIsNotReady.class);
    }
    
    @Test
    public void givenOrderWithLowTotalAmount_WhenAddingLoyaltyPoints_ShouldNotAddPoints() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder().withItems(false).status(OrderStatus.DRAFT).build();
        order.addItem(ProductTestDataBuilder.aProductAltRamMemory().build(), new Quantity(1));
        order.place();
        order.markAsPaid();
        order.markAsReady();
        orders.add(order);
        
        service.addLoyaltyPoints(customer.id().value(), order.id().toString());
        
        Customer updatedCustomer = customers.ofId(customer.id()).orElseThrow();

        Assertions.assertThat(updatedCustomer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(0));
    }
    
}
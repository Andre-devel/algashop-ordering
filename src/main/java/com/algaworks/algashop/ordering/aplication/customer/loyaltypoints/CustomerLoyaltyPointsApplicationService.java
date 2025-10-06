package com.algaworks.algashop.ordering.aplication.customer.loyaltypoints;

import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerLoyaltyPointsService;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.customer.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerLoyaltyPointsApplicationService {
    
    private final Customers customers;
    private final Orders orders;
    private final CustomerLoyaltyPointsService customerLoyaltyPointsService;

    public void addLoyaltyPoints(UUID rawCustomerId, String rawOrderId) {
        Objects.requireNonNull(rawCustomerId);
        Objects.requireNonNull(rawOrderId);
        
        Customer customer = customers.ofId(new CustomerId(rawCustomerId)).orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + rawCustomerId));
        Order order = orders.ofId(new OrderId(rawOrderId)).orElseThrow(() -> new OrderNotFoundException("Order not found: " + rawOrderId));
        
        customerLoyaltyPointsService.addPoints(customer, order);

        customers.add(customer);
    }
}

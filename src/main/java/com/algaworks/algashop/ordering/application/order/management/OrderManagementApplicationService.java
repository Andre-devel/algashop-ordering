package com.algaworks.algashop.ordering.application.order.management;

import com.algaworks.algashop.ordering.domain.model.customer.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class OrderManagementApplicationService {
    
    private final Orders orders;
    
    public void cancel(Long rawOrderId) {
        Objects.requireNonNull(rawOrderId);
        
        Order order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + rawOrderId));
        
        order.cancel();
        
        orders.add(order);
    }
    
    public void markAsPaid(Long rawOrderId) {
        Objects.requireNonNull(rawOrderId);
        
        Order order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + rawOrderId));
        
        order.markAsPaid();
        
        orders.add(order);
    }
    
    public void markAsReady(Long rawOrderId) {
        Objects.requireNonNull(rawOrderId);
        
        Order order = orders.ofId(new OrderId(rawOrderId))
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + rawOrderId));
        
        order.markAsReady();
        
        orders.add(order);
    }
}

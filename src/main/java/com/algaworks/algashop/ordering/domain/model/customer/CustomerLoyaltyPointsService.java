package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.order.Order;
import com.algaworks.algashop.ordering.domain.model.order.OrderNotBelongToCustomerException;

import java.util.Objects;

@DomainService
public class CustomerLoyaltyPointsService {
    
    private static final LoyaltyPoints basePoints = new LoyaltyPoints(5);

    private static final Money expectedAmountToGivenPoints = new Money("1000");

    public void addPoints(Customer customer, Order order) {
        Objects.requireNonNull(customer);
        Objects.requireNonNull(order);
        
        if (!customer.id().equals(order.customerId())) {
            throw new OrderNotBelongToCustomerException(); 
        }
        
        if (!order.isReady()) {
            throw new CantAddLoyaltyPointsOrderIsNotReady();
        }
        
        customer.addLoyaltyPoints(calculatePoints(order));
        
    }

    private LoyaltyPoints calculatePoints(Order order) {
        if (shouldGivePointsByAmount(order.totalAmount())) {
            Money result = order.totalAmount().divide(expectedAmountToGivenPoints);
            return new LoyaltyPoints(result.value().intValue() * basePoints.value());
        }
        
        return LoyaltyPoints.ZERO;
    }

    private boolean shouldGivePointsByAmount(Money amount) {
        return amount.compareTo(expectedAmountToGivenPoints) >= 0;
    }
}

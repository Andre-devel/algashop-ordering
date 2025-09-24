package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerAlreadyHasShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.utility.DomainService;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;

import java.util.Optional;

@DomainService
public class ShoppingService {
    
    private final Customers customers;
    private final ShoppingCarts shoppingCarts;

    public ShoppingService(Customers customers, ShoppingCarts shoppingCarts) {
        this.customers = customers;
        this.shoppingCarts = shoppingCarts;
    }

    public ShoppingCart startShopping(CustomerId customerId) {
        Optional<Customer> customerOptional = customers.ofId(customerId);
        
        if (customerOptional.isEmpty()) {
            throw new CustomerNotFoundException("Customer not found for id: " + customerId);
        }

        Optional<ShoppingCart> shoppingCart = shoppingCarts.ofCustomer(customerId);
        
        if (shoppingCart.isPresent()) {
            throw new CustomerAlreadyHasShoppingCartException("Customer already has a shopping cart");
        }
        
        return ShoppingCart.startShopping(customerId);
    }
}

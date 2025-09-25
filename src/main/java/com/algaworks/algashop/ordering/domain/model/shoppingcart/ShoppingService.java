package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.DomainService;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerAlreadyHasShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;

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

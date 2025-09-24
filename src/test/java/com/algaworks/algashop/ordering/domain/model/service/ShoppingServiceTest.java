package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerAlreadyHasShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.exception.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ShoppingServiceTest {
    
    @InjectMocks
    private ShoppingService shoppingService;

    @Mock
    private Customers customers;

    @Mock
    private ShoppingCarts shoppingCarts;
    
    @Test
    public void shouldStartShoppingSuccessfully() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        
        Mockito.when(shoppingCarts.ofCustomer(Mockito.any(CustomerId.class)))
                .thenReturn(Optional.empty());
        Mockito.when(customers.ofId(Mockito.any(CustomerId.class)))
                .thenReturn(Optional.of(customer));

        ShoppingCart shoppingCart = shoppingService.startShopping(customer.id());

        Assertions.assertThat(shoppingCart.customerId()).isEqualTo(customer.id());
    }
    
    @Test
    public void shouldThrowCustomerNotFoundExceptionWhenCustomerDoesNotExist() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        
        Mockito.when(customers.ofId(Mockito.any(CustomerId.class)))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> shoppingService.startShopping(customer.id()))
                .isInstanceOf(CustomerNotFoundException.class);
    }
    
    @Test
    public void shouldThrowCustomerAlreadyHasShoppingCartExceptionWhenCustomerAlreadyHasShoppingCart() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        ShoppingCart shoppingCart = ShoppingCart.startShopping(customer.id());
        
        Mockito.when(customers.ofId(Mockito.any(CustomerId.class)))
                .thenReturn(Optional.of(customer));
        Mockito.when(shoppingCarts.ofCustomer(Mockito.any(CustomerId.class)))
                .thenReturn(Optional.of(shoppingCart));

        Assertions.assertThatThrownBy(() -> shoppingService.startShopping(customer.id()))
                .isInstanceOf(CustomerAlreadyHasShoppingCartException.class);
    }
}
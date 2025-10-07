package com.algaworks.algashop.ordering.application.shoppingcart.query;

import com.algaworks.algashop.ordering.application.shoppingcart.management.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
@Transactional
class ShoppingCartQueryServiceIT {

    @Autowired
    private ShoppingCartQueryService shoppingCartQueryService;
    
    @Autowired
    private ShoppingCarts shoppingCarts;
    
    @Autowired
    private Customers customers;

    @BeforeEach
    public void setup() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }
    
    @Test
    public void shouldFindById() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        shoppingCarts.add(shoppingCart);
        
        ShoppingCartOutput output = shoppingCartQueryService.findById(shoppingCart.id().value());
        
        Assertions.assertThat(output)
                .extracting(
                        ShoppingCartOutput::getId,
                        ShoppingCartOutput::getCustomerId,
                        ShoppingCartOutput::getTotalItems,
                        ShoppingCartOutput::getTotalAmount
                ).containsExactly(
                        shoppingCart.id().value(),
                        shoppingCart.customerId().value(),
                        shoppingCart.totalItems().value(),
                        shoppingCart.totalAmount().value()
                );

    }
    
    @Test
    public void shouldShowExceptionWhenNotFoundById() {
        Assertions.assertThatThrownBy(() -> {
            shoppingCartQueryService.findById(UUID.randomUUID());
        }).isInstanceOf(ShoppingCartNotFoundException.class);
    }
    
    @Test
    public void shouldFindByCustomerId() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        CustomerId customerId = shoppingCart.customerId();
        shoppingCarts.add(shoppingCart);
        
        ShoppingCartOutput output = shoppingCartQueryService.findByCustomerId(customerId.value());
        
        Assertions.assertThat(output)
                .extracting(
                        ShoppingCartOutput::getId,
                        ShoppingCartOutput::getCustomerId,
                        ShoppingCartOutput::getTotalItems,
                        ShoppingCartOutput::getTotalAmount
                ).containsExactly(
                        shoppingCart.id().value(),
                        customerId.value(),
                        shoppingCart.totalItems().value(),
                        shoppingCart.totalAmount().value()
                );

    }
    
    @Test
    public void shouldShowExceptionWhenNotFoundByCustomerId() {
        Assertions.assertThatThrownBy(() -> {
            shoppingCartQueryService.findByCustomerId(UUID.randomUUID());
        }).isInstanceOf(ShoppingCartNotFoundException.class);
    }
}
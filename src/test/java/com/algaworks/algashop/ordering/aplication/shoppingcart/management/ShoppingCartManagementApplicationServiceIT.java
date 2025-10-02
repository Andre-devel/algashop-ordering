package com.algaworks.algashop.ordering.aplication.shoppingcart.management;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerAlreadyHasShoppingCartException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductCatalogService;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import com.algaworks.algashop.ordering.domain.model.product.ProductNotFoundException;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartCreatedEvent;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartDoesNotContainProductException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartEmptiedEvent;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemAddedEvent;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItemRemovedEvent;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import com.algaworks.algashop.ordering.infrastructure.listener.shoppingcart.ShoppingCartEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Transactional
@SpringBootTest
class ShoppingCartManagementApplicationServiceIT {
    
    @Autowired
    private ShoppingCartManagementApplicationService shoppingCartManagementApplicationService;
    
    @MockitoBean
    private ProductCatalogService productCatalogService;
    
    @Autowired
    private ShoppingCarts shoppingCarts;
    
    @Autowired
    private Customers customers;

    @MockitoSpyBean
    private ShoppingCartEventListener shoppingCartEventListener;

    @BeforeEach
    public void setup() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }
    
    @Test
    public void shouldAddItemToShoppingCart() {
        Mockito.when(productCatalogService.ofId(Mockito.any(ProductId.class))).thenReturn(Optional.of(ProductTestDataBuilder.aProduct().inStock(true).build()));
        
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        shoppingCarts.add(shoppingCart);

        Product product = ProductTestDataBuilder.aProduct().inStock(true).build();

        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder().productId(product.id().value())
                .quantity(2)
                .shoppingCartId(shoppingCart.id().value())
                .build();
        
        shoppingCartManagementApplicationService.addItem(shoppingCartItemInput);
        
        ShoppingCart updatedShoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();
        Assertions.assertThat(updatedShoppingCart.items().size()).isEqualTo(1);
    }
    
    @Test
    public void givenShoppingCartNotExists_whenAddItem_thenThrowException() {
        Product product = ProductTestDataBuilder.aProduct().inStock(true).build();

        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder().productId(product.id().value())
                .quantity(2)
                .shoppingCartId(java.util.UUID.randomUUID())
                .build();
        
        Assertions.assertThatThrownBy(() -> {
            shoppingCartManagementApplicationService.addItem(shoppingCartItemInput);
        }).isInstanceOf(ShoppingCartNotFoundException.class);
    }
    
    @Test
    public void givenProductNotExists_whenAddItem_thenThrowException() {
        Mockito.when(productCatalogService.ofId(Mockito.any(ProductId.class))).thenReturn(Optional.empty());
        
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        shoppingCarts.add(shoppingCart);

        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder()
                .productId(UUID.randomUUID())
                .quantity(2)
                .shoppingCartId(shoppingCart.id().value())
                .build();
        
        Assertions.assertThatThrownBy(() -> {
            shoppingCartManagementApplicationService.addItem(shoppingCartItemInput);
        }).isInstanceOf(ProductNotFoundException.class);
    }
    
    @Test
    public void givenProductOutOfStock_whenAddItem_thenThrowException() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        shoppingCarts.add(shoppingCart);

        Product product = ProductTestDataBuilder.aProduct().inStock(false).build();

        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder().productId(product.id().value())
                .quantity(2)
                .shoppingCartId(shoppingCart.id().value())
                .build();
        
        Assertions.assertThatThrownBy(() -> {
            shoppingCartManagementApplicationService.addItem(shoppingCartItemInput);
        }).isInstanceOf(ProductNotFoundException.class);
    }
    
    @Test
    public void shouldCreateNewShoppingCart() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        
        UUID shoppingCartId = shoppingCartManagementApplicationService.createNew(customer.id().value());
        
        Assertions.assertThat(shoppingCartId).isNotNull();
        Assertions.assertThat(shoppingCarts.ofId(new ShoppingCartId(shoppingCartId))).isPresent();
    }
    
    @Test
    public void givenCustomerNotExists_whenCreateNewShoppingCart_thenThrowException() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).build();
        
        Assertions.assertThatThrownBy(() -> {
            shoppingCartManagementApplicationService.createNew(customer.id().value());
        }).isInstanceOf(CustomerNotFoundException.class);
    }
    
    @Test
    public void givenCustomerAlreadyHasShoppingCart_whenCreateNewShoppingCart_thenThrowException() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
        shoppingCarts.add(shoppingCart);
        
        Assertions.assertThatThrownBy(() -> {
            shoppingCartManagementApplicationService.createNew(customer.id().value());
        }).isInstanceOf(CustomerAlreadyHasShoppingCartException.class);
    }
    
    @Test
    public void shouldRemoveItemFromShoppingCart() {
        Product product = ProductTestDataBuilder.aProduct().inStock(true).build();
        Product anotherProduct = ProductTestDataBuilder.aProductAltMousePad().id(new ProductId()).inStock(true).build();
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        
        shoppingCart.addItem(product, new Quantity(1));
        shoppingCart.addItem(anotherProduct, new Quantity(1));
        
        ProductId productId = product.id();
        ProductId anotherProductId = anotherProduct.id();
        
        shoppingCarts.add(shoppingCart);
        
        Assertions.assertThat(shoppingCart.findItem(productId)).isNotNull();
        Assertions.assertThat(shoppingCart.findItem(anotherProductId)).isNotNull();
        Assertions.assertThat(shoppingCart.items().size()).isEqualTo(2);
        
        UUID shoppingCartItemId = shoppingCart.findItem(productId).id().value();
        
        shoppingCartManagementApplicationService.removeItem(shoppingCart.id().value(), shoppingCartItemId);
        
        ShoppingCart updatedShoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();
        
        Assertions.assertThat(updatedShoppingCart.items().size()).isEqualTo(1);
        Assertions.assertThatThrownBy(() -> {
            updatedShoppingCart.findItem(productId);
        }).isInstanceOf(ShoppingCartDoesNotContainProductException.class);
    }
    
    @Test
    public void shouldEmptyShoppingCart() {
        Product product = ProductTestDataBuilder.aProduct().inStock(true).build();
        Product anotherProduct = ProductTestDataBuilder.aProductAltMousePad().id(new ProductId()).inStock(true).build();
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        
        shoppingCart.addItem(product, new Quantity(1));
        shoppingCart.addItem(anotherProduct, new Quantity(1));
        
        shoppingCarts.add(shoppingCart);
        
        Assertions.assertThat(shoppingCart.items().size()).isEqualTo(2);
        
        shoppingCartManagementApplicationService.empty(shoppingCart.id().value());
        
        ShoppingCart updatedShoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();
        
        Assertions.assertThat(updatedShoppingCart.items().size()).isEqualTo(0);
    }
    
    @Test
    public void shouldDeleteShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        shoppingCarts.add(shoppingCart);
        
        Assertions.assertThat(shoppingCarts.ofId(shoppingCart.id())).isPresent();
        
        shoppingCartManagementApplicationService.delete(shoppingCart.id().value());
        
        Assertions.assertThat(shoppingCarts.ofId(shoppingCart.id())).isNotPresent();
    }
    
    @Test
    public void givenShoppingCartDataValid_whenCreateNewShoppingCart_thenShouldPublishEvent() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        
        UUID shoppingCartId = shoppingCartManagementApplicationService.createNew(customer.id().value());
        
        Mockito.verify(shoppingCartEventListener, Mockito.times(1)).listen(Mockito.any(ShoppingCartCreatedEvent.class));
    }
    
    @Test
    public void givenShoppingCartDataValid_whenEmptyShoppingCart_thenShouldPublishEvent() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        shoppingCarts.add(shoppingCart);
        
        shoppingCartManagementApplicationService.empty(shoppingCart.id().value());
        
        Mockito.verify(shoppingCartEventListener, Mockito.times(1)).listen(Mockito.any(ShoppingCartEmptiedEvent.class));
    }
    
    @Test
    public void givenShoppingCartDataValid_whenAddItem_thenShouldPublishEvent() {
        Mockito.when(productCatalogService.ofId(Mockito.any(ProductId.class))).thenReturn(Optional.of(ProductTestDataBuilder.aProduct().inStock(true).build()));
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        
        shoppingCarts.add(shoppingCart);
        
        ShoppingCartItemInput shoppingCartItemInput = ShoppingCartItemInput.builder().
                productId(ProductTestDataBuilder.aProduct().inStock(true).build().id().value()).
                quantity(1).
                shoppingCartId(shoppingCart.id().value()).
                build();

        shoppingCartManagementApplicationService.addItem(shoppingCartItemInput);
        
        Mockito.verify(shoppingCartEventListener).listen(Mockito.any(ShoppingCartItemAddedEvent.class));
    }
    
    @Test
    public void givenShoppingCartDataValid_whenRemoveItem_thenShouldPublishEvent() {
        Product product = ProductTestDataBuilder.aProduct().inStock(true).build();
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().build();
        
        shoppingCart.addItem(product, new Quantity(1));
        
        ProductId productId = product.id();
        
        shoppingCarts.add(shoppingCart);
        
        Assertions.assertThat(shoppingCart.findItem(productId)).isNotNull();
        Assertions.assertThat(shoppingCart.items().size()).isEqualTo(1);
        
        UUID shoppingCartItemId = shoppingCart.findItem(productId).id().value();
        
        shoppingCartManagementApplicationService.removeItem(shoppingCart.id().value(), shoppingCartItemId);
        
        Mockito.verify(shoppingCartEventListener, Mockito.times(1)).listen(Mockito.any(ShoppingCartItemRemovedEvent.class));
    }
}
package com.algaworks.algashop.ordering.application.checkout;

import com.algaworks.algashop.ordering.application.order.query.BillingData;
import com.algaworks.algashop.ordering.application.shoppingcart.management.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import com.algaworks.algashop.ordering.domain.model.order.OrderPlacedEvent;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import com.algaworks.algashop.ordering.domain.model.order.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCarts;
import com.algaworks.algashop.ordering.infrastructure.listener.order.OrderEventListener;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional
@SpringBootTest
class CheckoutApplicationServiceIT {
    
    @Autowired
    private CheckoutApplicationService checkoutApplicationService;
    
    @Autowired
    private Orders orders;
    
    @Autowired
    private ShoppingCarts shoppingCarts;
    
    @Autowired
    private Customers customers;

    @MockitoSpyBean
    private OrderEventListener orderEventListener;

    @BeforeEach
    public void setup() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }
    
    @Test
    void shouldCheckoutSuccessfully() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(true).build();
        shoppingCarts.add(shoppingCart);
        
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        ShippingInput shippingInput = BuyNowInputTestDataBuilder.aShippingInput();
        BillingData billingData = BuyNowInputTestDataBuilder.aBillingData();

        
        
        CheckoutInput input = CheckoutInput.builder()
                .shoppingCartId(shoppingCart.id().value())
                .paymentMethod(paymentMethod.name())
                .shipping(shippingInput)
                .billing(billingData)
                .build();
        
        String orderId = checkoutApplicationService.checkout(input);
        ShoppingCart updatedCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();

        Assertions.assertThat(orders.ofId(new OrderId(orderId))).isPresent();
        Assertions.assertThat(updatedCart.isEmpty()).isTrue();
    }
    
    @Test
    void givenNonExistingShoppingCart_whenCheckout_thenThrowException() {
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        ShippingInput shippingInput = BuyNowInputTestDataBuilder.aShippingInput();
        BillingData billingData = BuyNowInputTestDataBuilder.aBillingData();

        CheckoutInput input = CheckoutInput.builder()
                .shoppingCartId(UUID.randomUUID())
                .paymentMethod(paymentMethod.name())
                .shipping(shippingInput)
                .billing(billingData)
                .build();
        
        Assertions.assertThatThrownBy(() -> 
            checkoutApplicationService.checkout(input)
        ).isInstanceOf(ShoppingCartNotFoundException.class);
    }
    
    @Test
    void givenEmptyShoppingCart_whenCheckout_thenThrowException() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        shoppingCarts.add(shoppingCart);
        
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        ShippingInput shippingInput = BuyNowInputTestDataBuilder.aShippingInput();
        BillingData billingData = BuyNowInputTestDataBuilder.aBillingData();

        CheckoutInput input = CheckoutInput.builder()
                .shoppingCartId(shoppingCart.id().value())
                .paymentMethod(paymentMethod.name())
                .shipping(shippingInput)
                .billing(billingData)
                .build();
        
        Assertions.assertThatThrownBy(() -> 
            checkoutApplicationService.checkout(input)
        ).isInstanceOf(ShoppingCartCantProceedToCheckoutException.class);
    }
    
    @Test
    void givenShoppingCartWithProductsOutOfStock_whenCheckout_thenThrowException() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(true).build();

        Product productUnavailable = ProductTestDataBuilder.aProductUnavailable().build();
        shoppingCart.refreshItem(productUnavailable);

        shoppingCarts.add(shoppingCart);

        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        ShippingInput shippingInput = BuyNowInputTestDataBuilder.aShippingInput();
        BillingData billingData = BuyNowInputTestDataBuilder.aBillingData();

        CheckoutInput input = CheckoutInput.builder()
                .shoppingCartId(shoppingCart.id().value())
                .paymentMethod(paymentMethod.name())
                .shipping(shippingInput)
                .billing(billingData)
                .build();

        Assertions.assertThatThrownBy(() ->
            checkoutApplicationService.checkout(input)
        ).isInstanceOf(ShoppingCartCantProceedToCheckoutException.class);
    }
    
    @Test
    void givenValidData_whenCheckout_thenEventShouldBeTriggered() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(true).build();
        shoppingCarts.add(shoppingCart);
        
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        ShippingInput shippingInput = BuyNowInputTestDataBuilder.aShippingInput();
        BillingData billingData = BuyNowInputTestDataBuilder.aBillingData();

        CheckoutInput input = CheckoutInput.builder()
                .shoppingCartId(shoppingCart.id().value())
                .paymentMethod(paymentMethod.name())
                .shipping(shippingInput)
                .billing(billingData)
                .build();
        
        checkoutApplicationService.checkout(input);
        
        Mockito.verify(orderEventListener)
                .listen(Mockito.any(OrderPlacedEvent.class));
    }
}
package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {
    
    private CheckoutService checkoutService;

    @Mock
    private Orders orders;

    @BeforeEach
    void setup() {
        CustomerHaveFreeShippingSpecification specification = new CustomerHaveFreeShippingSpecification(orders, new LoyaltyPoints(100), 2L, new LoyaltyPoints(2000));
        checkoutService = new CheckoutService(specification);
    }
    
    @Test
    void shouldCheckoutSuccessfully() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();
        
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        Assertions.assertThat(shoppingCart.containsUnavailableItems()).isFalse();
        
        Order order = checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod);

        ShoppingCart shoppingCart2 = ShoppingCartTestDataBuilder.aShoppingCart().build();

        Assertions.assertThat(order.customerId()).isNotNull();
        Assertions.assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        Assertions.assertThat(order.billing()).isEqualTo(billing);
        Assertions.assertThat(order.shipping()).isEqualTo(shipping);
        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.PLACED);
        
        Assertions.assertThat(order.totalAmount()).isEqualTo(shoppingCart2.totalAmount().add(shipping.cost()));
        Assertions.assertThat(order.totalItems()).isEqualTo(shoppingCart2.totalItems());
        Assertions.assertThat(order.items().size()).isEqualTo(shoppingCart2.items().size());
        
        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
    }
    
    @Test
    void shouldNotCheckoutWhenCartIsUnavailable() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();

        Product productUnavailable = ProductTestDataBuilder.aProductUnavailable().build();
        shoppingCart.refreshItem(productUnavailable);
        
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        Assertions.assertThatThrownBy(() -> {
            checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod);
        }).isInstanceOf(ShoppingCartCantProceedToCheckoutException.class);
        
        Assertions.assertThat(shoppingCart.isEmpty()).isFalse();
    }
    
    @Test
    void shouldNotCheckoutWhenCartIsEmpty() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id())
                .withItems(false)
                .build();
        
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        Assertions.assertThatThrownBy(() -> {
            checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod);
        }).isInstanceOf(ShoppingCartCantProceedToCheckoutException.class);
        
        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
    }

    @Test
    void givenShoppingCartAndCustomerWithFreeShipping_whenCheckout_ShouldReturnPlacedOrderWithFreeShipping() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().loyaltyPoints(new LoyaltyPoints(3000)).build();

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().customerId(customer.id()).build();

        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();

        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Assertions.assertThat(shoppingCart.containsUnavailableItems()).isFalse();

        Order order = checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod);

        ShoppingCart shoppingCart2 = ShoppingCartTestDataBuilder.aShoppingCart().build();

        Assertions.assertThat(order.customerId()).isNotNull();
        Assertions.assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        Assertions.assertThat(order.billing()).isEqualTo(billing);
        Assertions.assertThat(order.shipping()).isEqualTo(shipping.toBuilder().cost(Money.ZERO).build());
        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.PLACED);

        Assertions.assertThat(order.totalAmount()).isEqualTo(shoppingCart2.totalAmount());
        Assertions.assertThat(order.totalItems()).isEqualTo(shoppingCart2.totalItems());
        Assertions.assertThat(order.items().size()).isEqualTo(shoppingCart2.items().size());

        Assertions.assertThat(shoppingCart.isEmpty()).isTrue();
    }

}
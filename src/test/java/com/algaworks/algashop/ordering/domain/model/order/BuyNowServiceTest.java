package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

@ExtendWith(MockitoExtension.class)
class BuyNowServiceTest {
    @InjectMocks
    private BuyNowService buyNowService;
    
    @Mock
    private Orders orders;
    
    @Test
    void shouldBuyNowSuccessfully() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Product product = ProductTestDataBuilder.aProduct().inStock(true).build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = new Quantity(1);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Order order = buyNowService.buyNow(product, customer, billing, shipping, quantity, paymentMethod);
        
        Assertions.assertThat(order.totalItems()).isEqualTo(quantity);
        Assertions.assertThat(order.totalAmount()).isEqualTo(product.price().multiply(quantity).add(shipping.cost()));
        Assertions.assertThat(order.customerId()).isNotNull();
        Assertions.assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        Assertions.assertThat(order.billing()).isEqualTo(billing);
        Assertions.assertThat(order.shipping()).isEqualTo(shipping);
        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.PLACED);
    }
    
    @Test
    void shouldNotBuyNowWhenProductIsOutOfStock() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Product product = ProductTestDataBuilder.aProductUnavailable().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = new Quantity(1);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        Assertions.assertThatThrownBy(() -> {
            buyNowService.buyNow(product, customer, billing, shipping, quantity, paymentMethod);
        }).isInstanceOf(ProductOutOfStockException.class);
    }
    
    @Test
    void shouldNotBuyNowWhenQuantityIsZero() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Product product = ProductTestDataBuilder.aProduct().inStock(true).build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = new Quantity(0);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        
        Assertions.assertThatThrownBy(() -> {
            buyNowService.buyNow(product, customer, billing, shipping, quantity, paymentMethod);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenCustomerWithFreeShipping_whenBuyNow_ShouldReturnPlacedOrderWithFreeShipping() {
        Mockito.when(orders.salesQuantityByCustomerInYear(Mockito.any(CustomerId.class), Mockito.any(Year.class)))
                .thenReturn(2L);
        Customer customer = CustomerTestDataBuilder.existingCustomer().loyaltyPoints(new LoyaltyPoints(100)).build();
        Product product = ProductTestDataBuilder.aProduct().inStock(true).build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = new Quantity(1);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Order order = buyNowService.buyNow(product, customer, billing, shipping, quantity, paymentMethod);

        Assertions.assertThat(order.totalItems()).isEqualTo(quantity);
        Assertions.assertThat(order.customerId()).isNotNull();
        Assertions.assertThat(order.paymentMethod()).isEqualTo(paymentMethod);
        Assertions.assertThat(order.billing()).isEqualTo(billing);
        Assertions.assertThat(order.shipping()).isEqualTo(shipping.toBuilder().cost(Money.ZERO).build());
        Assertions.assertThat(order.status()).isEqualTo(OrderStatus.PLACED);
        
        Assertions.assertThat(order.totalAmount()).isEqualTo(product.price().multiply(quantity));
    }
}
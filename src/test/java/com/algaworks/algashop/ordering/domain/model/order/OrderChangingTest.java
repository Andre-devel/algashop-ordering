package com.algaworks.algashop.ordering.domain.model.order;

import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.assertj.core.api.Assertions;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import org.junit.jupiter.api.Test;

public class OrderChangingTest {


    @Test
    void givenDraftOrder_whenChangeIsPerformed_shouldNotThrowException() {
        Order order = Order.draft(new CustomerId());
        Product product = ProductTestDataBuilder.aProductAltRamMemory().build();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Billing billing = OrderTestDataBuilder.aBilling();
        PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;

        order.addItem(product, new Quantity(1));
        order.changeShipping(shipping);
        order.changeBilling(billing);
        order.changePaymentMethod(paymentMethod);

        Assertions.assertWith(order,
                (o) -> Assertions.assertThat(o.items()).isNotEmpty(),
                (o) -> Assertions.assertThat(o.shipping()).isEqualTo(shipping),
                (o) -> Assertions.assertThat(o.billing()).isEqualTo(billing),
                (o) -> Assertions.assertThat(o.paymentMethod()).isEqualTo(paymentMethod)
        );
    }

    @Test
    void givenPlacedOrder_whenChangeBillingIsCalled_shouldThrowOrderCannotBeEditedException() {
        Order placedOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        Billing billing = OrderTestDataBuilder.aBilling();

        assertThatThrownBy(() -> placedOrder.changeBilling(billing))
                .isInstanceOf(OrderCannotBeEditedException.class);
    }

    @Test
    void givenPlacedOrder_whenChangeShippingIsCalled_shouldThrowOrderCannotBeEditedException() {
        Order placedOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        Shipping shipping = OrderTestDataBuilder.aShipping();

        assertThatThrownBy(() -> placedOrder.changeShipping(shipping))
                .isInstanceOf(OrderCannotBeEditedException.class);
    }

    @Test
    void givenPlacedOrder_whenChangeItemQuantityIsCalled_shouldThrowOrderCannotBeEditedException() {
        Order placedOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        Quantity quantity = new Quantity(5);

        OrderItem orderItem = placedOrder.items().iterator().next();

        assertThatThrownBy(() -> placedOrder.changeItemQuantity(orderItem.id(), quantity))
                .isInstanceOf(OrderCannotBeEditedException.class);
    }

    @Test
    void givenPlacedOrder_whenChangePaymentMethodIsCalled_shouldThrowOrderCannotBeEditedException() {
        Order placedOrder = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        PaymentMethod method = PaymentMethod.GATEWAY_BALANCE;

        assertThatThrownBy(() -> placedOrder.changePaymentMethod(method))
                .isInstanceOf(OrderCannotBeEditedException.class);
    }
}

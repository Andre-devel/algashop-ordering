package com.algaworks.algashop.ordering.domain.model.entity;

import com.algaworks.algashop.ordering.domain.model.exception.OrderCannotBeEditedException;
import com.algaworks.algashop.ordering.domain.model.exception.OrderDoesNotContainOrderItemException;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;


public class OrderRemoveItemTest {
    
    @Test
    public void givenDraftOrder_whenRemoveItem_shouldRecalculate() {
        Order order = Order.draft(new CustomerId());
        order.addItem(ProductTestDataBuilder.aProductAltMousePad().price(new Money("100")).build(), new Quantity(3));
        order.addItem(ProductTestDataBuilder.aProductAltRamMemory().price(new Money("200")).build(), new Quantity(1));
        order.addItem(ProductTestDataBuilder.aProduct().price(new Money("3000")).build(), new Quantity(2));
        
        OrderItem orderItem = order.items().stream().findFirst().orElseThrow();
        
        int finalQuantity = order.totalItems().value() - orderItem.quantity().value();
        BigDecimal finalAmount = order.totalAmount().value().subtract(orderItem.totalAmount().value());
        
        order.removeItem(orderItem.id());
        
        Assertions.assertThat(order.totalItems()).isEqualTo(new Quantity(finalQuantity));
        Assertions.assertThat(order.totalAmount()).isEqualTo(new Money(finalAmount));
    }

    @Test
    void givenDraftOrder_whenTryToRemoveNoExistingItem_shouldGenerateException() {
        Order order = OrderTestDataBuilder.anOrder().build();

        Assertions.assertThatExceptionOfType(OrderDoesNotContainOrderItemException.class)
                .isThrownBy(()-> order.removeItem(new OrderItemId()));

        Assertions.assertWith(order,
                (i) -> Assertions.assertThat(i.totalAmount()).isEqualTo(new Money("6210.00")),
                (i) -> Assertions.assertThat(i.totalItems()).isEqualTo(new Quantity(3))
        );
    }

    @Test
    void givenPlacedOrder_whenTryToRemoveItem_shouldGenerateException() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();

        Assertions.assertThatExceptionOfType(OrderCannotBeEditedException.class)
                .isThrownBy(()->order.removeItem(new OrderItemId()));

        Assertions.assertWith(order,
                (i) -> Assertions.assertThat(i.totalAmount()).isEqualTo(new Money("6210.00")),
                (i) -> Assertions.assertThat(i.totalItems()).isEqualTo(new Quantity(3))
        );
    }
}


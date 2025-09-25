package com.algaworks.algashop.ordering.domain.model.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class OrderIsCanceledTest {
    
    @Test
    public void givenCancelOrder_whenThyToCancel_shouldGenerateException() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).build();

        Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                .isThrownBy(order::cancel);
    }
    
    @Test
    public void givenCancelOrder_whenVerifyStatus_shouldAllow() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).build();

        Assertions.assertThat(order.isCanceled()).isFalse();
        order.place();
        Assertions.assertThat(order.isCanceled()).isFalse();
        order.markAsPaid();
        Assertions.assertThat(order.isCanceled()).isFalse();
        order.markAsReady();
        Assertions.assertThat(order.isCanceled()).isFalse();
        order.cancel();
        Assertions.assertThat(order.isCanceled()).isTrue();
    }
}

package com.algaworks.algashop.ordering.infrastructure.persistence.assembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class OrderPersistenceEntityAssemblerTest {
    
    private final OrderPersistenceEntityAssembler assembler = new OrderPersistenceEntityAssembler();
    
    @Test
    void shouldConvertToDomain() {
        Order order = OrderTestDataBuilder.anOrder().build();
        OrderPersistenceEntity orderPersistenceEntity = assembler.fromDomain(order);

        assertThat(orderPersistenceEntity).satisfies(
                e -> assertThat(e.getId()).isEqualTo(order.id().value().toLong()),
                e -> assertThat(e.getCustomerId()).isEqualTo(order.customerId().value()),
                e -> assertThat(e.getTotalAmount()).isEqualTo(order.totalAmount().value()),
                e -> assertThat(e.getTotalItems()).isEqualTo(order.totalItems().value()),
                e -> assertThat(e.getStatus()).isEqualTo(order.status().name()),
                e -> assertThat(e.getPaymentMethod()).isEqualTo(order.paymentMethod().name()),
                e -> assertThat(e.getPlaceAt()).isEqualTo(order.placedAt()),
                e -> assertThat(e.getPaidAt()).isEqualTo(order.paidAt()),
                e -> assertThat(e.getCancelAt()).isEqualTo(order.cancelAt()),
                e -> assertThat(e.getReadyAt()).isEqualTo(order.readyAt())
        );
    }

}
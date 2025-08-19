
package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.PaymentMethod;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntityTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderPersistenceEntityDisassemblerTest {
    
    private final OrderPersistenceEntityDisassembler disassembler = new OrderPersistenceEntityDisassembler();
    
    @Test
    public void shouldConvertFromPersistence() {
        OrderPersistenceEntity persistenceEntity = OrderPersistenceEntityTestDataBuilder.existingOrder().build();
        Order domainEntity = disassembler.toDomainEntity(persistenceEntity);

        Assertions.assertThat(domainEntity).satisfies(
                e -> Assertions.assertThat(e.id()).isEqualTo(new OrderId(persistenceEntity.getId()))
                , e -> Assertions.assertThat(e.customerId()).isEqualTo(new CustomerId(persistenceEntity.getCustomerId()))
                , e -> Assertions.assertThat(e.totalAmount()).isEqualTo(new Money(persistenceEntity.getTotalAmount()))
                , e -> Assertions.assertThat(e.totalItems()).isEqualTo(new Quantity(persistenceEntity.getTotalItems()))
                , e -> Assertions.assertThat(e.status()).isEqualTo(OrderStatus.valueOf(persistenceEntity.getStatus()))
                , e -> Assertions.assertThat(e.paymentMethod()).isEqualTo(PaymentMethod.valueOf(persistenceEntity.getPaymentMethod()))
                , e -> Assertions.assertThat(e.placedAt()).isEqualTo(persistenceEntity.getPlaceAt())
                , e -> Assertions.assertThat(e.paidAt()).isEqualTo(persistenceEntity.getPaidAt())
                , e -> Assertions.assertThat(e.cancelAt()).isEqualTo(persistenceEntity.getCancelAt())
                , e -> Assertions.assertThat(e.readyAt()).isEqualTo(persistenceEntity.getReadyAt())
        );
    }

}
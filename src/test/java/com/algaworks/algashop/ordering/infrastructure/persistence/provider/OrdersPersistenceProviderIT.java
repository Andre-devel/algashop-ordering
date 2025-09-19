package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.entity.Order;
import com.algaworks.algashop.ordering.domain.model.entity.OrderStatus;
import com.algaworks.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import({
        OrdersPersistenceProvider.class, 
        OrderPersistenceEntityAssembler.class, 
        OrderPersistenceEntityDisassembler.class,
        SpringDataAuditingConfig.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityDisassembler.class,
        CustomerPersistenceEntityAssembler.class
})
class OrdersPersistenceProviderIT {
    
    private final OrdersPersistenceProvider ordersPersistenceProvider;
    private final OrderPersistenceEntityRepository persistenceRepository;
    private final CustomersPersistenceProvider customersPersistenceProvider;
    
    @Autowired
    public OrdersPersistenceProviderIT(OrdersPersistenceProvider ordersPersistenceProvider, OrderPersistenceEntityRepository persistenceRepository, CustomersPersistenceProvider customersPersistenceProvider) {
        this.ordersPersistenceProvider = ordersPersistenceProvider;
        this.persistenceRepository = persistenceRepository;
        this.customersPersistenceProvider = customersPersistenceProvider;
    }
    @BeforeEach
    public void setup() {
        if (!customersPersistenceProvider.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customersPersistenceProvider.add(
                    CustomerTestDataBuilder.existingCustomer().build()
            );
        }
    }
    
    @Test
    public void shouldUpdateAndKeepPersistenceEntityState() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        Long orderId = order.id().value().toLong();
        ordersPersistenceProvider.add(order);
        
        OrderPersistenceEntity persistenceEntity = persistenceRepository.findById(orderId).orElseThrow();
        
        Assertions.assertThat(persistenceEntity.getStatus()).isEqualTo(OrderStatus.PLACED.name());
        
        Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();
        
        order = ordersPersistenceProvider.ofId(order.id()).orElseThrow();
        order.markAsPaid();
        ordersPersistenceProvider.add(order);
        
        order = ordersPersistenceProvider.ofId(order.id()).orElseThrow();
        
        Assertions.assertThat(persistenceEntity.getStatus()).isEqualTo(OrderStatus.PAID.name());

        Assertions.assertThat(persistenceEntity.getCreatedByUserId()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedAt()).isNotNull();
        Assertions.assertThat(persistenceEntity.getLastModifiedByUserId()).isNotNull();
    }
    
    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldAddFindAndNotFailWHenNoTransaction() {
        Order order = OrderTestDataBuilder.anOrder().build();
        ordersPersistenceProvider.add(order);
        
        Assertions.assertThatNoException().isThrownBy(
                () -> ordersPersistenceProvider.ofId(order.id()).orElseThrow()
        );
    }
}
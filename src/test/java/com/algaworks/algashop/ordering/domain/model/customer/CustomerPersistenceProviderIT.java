package com.algaworks.algashop.ordering.domain.model.customer;

import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.provider.CustomersPersistenceProvider;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@DataJpaTest
@Import({
        CustomersPersistenceProvider.class,
        CustomerPersistenceEntityAssembler.class,
        CustomerPersistenceEntityDisassembler.class,
        SpringDataAuditingConfig.class
})
public class CustomerPersistenceProviderIT {
    
    private final CustomersPersistenceProvider customersPersistenceProvider;
    private final CustomerPersistenceEntityRepository persistenceRepository;

    @Autowired
    public CustomerPersistenceProviderIT(CustomersPersistenceProvider customersPersistenceProvider, CustomerPersistenceEntityRepository persistenceRepository) {
        this.customersPersistenceProvider = customersPersistenceProvider;
        this.persistenceRepository = persistenceRepository;
    }

    @Test
    public void shouldUpdateAndKeepPersistenceEntity() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().id(new CustomerId()).build();
        UUID customerId = customer.id().value();
        
        customersPersistenceProvider.add(customer);
        
        CustomerPersistenceEntity savedCustomer = persistenceRepository.findById(customer.id().value()).orElseThrow();

        Assertions.assertThat(customerId).isEqualTo(savedCustomer.getId());
        Assertions.assertThat(customer.email().value()).isEqualTo(savedCustomer.getEmail());

        Assertions.assertThat(savedCustomer.getCreatedByUserId()).isNotNull();
        Assertions.assertThat(savedCustomer.getLastModifiedAt()).isNotNull();
        Assertions.assertThat(savedCustomer.getLastModifiedByUserId()).isNotNull();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldAddFindAndNotFailWHenNoTransaction() {
        Customer order = CustomerTestDataBuilder.existingCustomer().build();
        customersPersistenceProvider.add(order);

        Assertions.assertThatNoException().isThrownBy(
                () -> customersPersistenceProvider.ofId(order.id()).orElseThrow()
        );
    }
}

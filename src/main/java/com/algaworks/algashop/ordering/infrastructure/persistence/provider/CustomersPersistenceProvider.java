package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.entity.Customer;
import com.algaworks.algashop.ordering.domain.model.repository.Customers;
import com.algaworks.algashop.ordering.domain.model.valueobject.Email;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.CustomerPersistenceEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomersPersistenceProvider implements Customers {
    
    private final CustomerPersistenceEntityRepository persistenceRepository;
    public final CustomerPersistenceEntityDisassembler disassembler;
    public final CustomerPersistenceEntityAssembler assembler;

    private final EntityManager entityManager;
    
    
    @Override
    public Optional<Customer> ofId(CustomerId customerId) {
        Optional<CustomerPersistenceEntity> possibleEntity = persistenceRepository.findById(customerId.value());
        
        return possibleEntity.map(disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(CustomerId customerId) {
        return persistenceRepository.existsById(customerId.value());
    }

    @Override
    public void add(Customer aggregateRoot) {
        UUID valueId = aggregateRoot.id().value();
        persistenceRepository.findById(valueId)
                .ifPresentOrElse(
                        existingEntity -> update(aggregateRoot, existingEntity),
                        () -> insert(aggregateRoot)
                );
        
    }

    private void insert(Customer aggregateRoot) {
        CustomerPersistenceEntity savedEntity = persistenceRepository.saveAndFlush(assembler.fromDomain(aggregateRoot));
        updateVersion(aggregateRoot, savedEntity);
    }

    private void update(Customer aggregateRoot, CustomerPersistenceEntity existingEntity) {
        CustomerPersistenceEntity persistenceEntity = assembler.merge( existingEntity, aggregateRoot);
        entityManager.detach(persistenceEntity);
        CustomerPersistenceEntity savedEntity = persistenceRepository.saveAndFlush(persistenceEntity);
        updateVersion(aggregateRoot, savedEntity);
    }

    @Override
    public Long count() {
        return persistenceRepository.count();
    }

    @SneakyThrows
    private void updateVersion(Customer aggregateRoot, CustomerPersistenceEntity persistenceEntity) {
        Field version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);

        ReflectionUtils.setField(version, aggregateRoot, persistenceEntity.getVersion());

        version.setAccessible(false);
    }

    @Override
    public Optional<Customer> ofEmail(Email email) {
       return persistenceRepository.findByEmail(email.value()).map(disassembler::toDomainEntity);
    }
}

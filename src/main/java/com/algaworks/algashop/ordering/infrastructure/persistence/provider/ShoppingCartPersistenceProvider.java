package com.algaworks.algashop.ordering.infrastructure.persistence.provider;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.repository.ShoppingCarts;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.infrastructure.persistence.assembler.ShoppingCartPersistenceEntityAssembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.disassembler.ShoppingCartPersistenceEntityDisassembler;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.repository.ShoppingCartPersistenceEntityRepository;
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
public class ShoppingCartPersistenceProvider implements ShoppingCarts {
    
    private final ShoppingCartPersistenceEntityRepository persistenceEntityRepository;      
    private final ShoppingCartPersistenceEntityAssembler assembler;
    private final ShoppingCartPersistenceEntityDisassembler disassembler;

    private final EntityManager entityManager;
    
    @Override
    public Optional<ShoppingCart> ofCustomer(CustomerId customerId) {
        Optional<ShoppingCartPersistenceEntity> possibleEntity = persistenceEntityRepository.findByCustomer_Id(customerId.value());
        
        return possibleEntity.map(disassembler::toDomainEntity);
    }

    @Override
    public void remove(ShoppingCartId shoppingCartId) {
        persistenceEntityRepository.deleteById(shoppingCartId.value());
    }

    @Override
    public Optional<ShoppingCart> ofId(ShoppingCartId shoppingCartId) {
        Optional<ShoppingCartPersistenceEntity> possibleEntity = persistenceEntityRepository.findById(shoppingCartId.value());     
        
        return possibleEntity.map(disassembler::toDomainEntity);
    }

    @Override
    public boolean exists(ShoppingCartId shoppingCartId) {
        return persistenceEntityRepository.existsById(shoppingCartId.value());  
    }

    @Override
    @Transactional(readOnly = false)
    public void add(ShoppingCart aggregateRoot) {
        UUID valueId = aggregateRoot.id().value();
        
        persistenceEntityRepository.findById(valueId)
                .ifPresentOrElse(
                        existingEntity -> update(aggregateRoot, existingEntity),
                        () -> insert(aggregateRoot)
                );
    }

    private void insert(ShoppingCart aggregateRoot) {
            ShoppingCartPersistenceEntity persistenceEntity = assembler.fromDomain(aggregateRoot);
        ShoppingCartPersistenceEntity savedEntity = persistenceEntityRepository.saveAndFlush(persistenceEntity);
        updateVersion(aggregateRoot, savedEntity);
    }

    private void update(ShoppingCart aggregateRoot, ShoppingCartPersistenceEntity existingEntity) {
        ShoppingCartPersistenceEntity persistenceEntity = assembler.merge(existingEntity, aggregateRoot);
        entityManager.detach(persistenceEntity);
        persistenceEntity = persistenceEntityRepository.saveAndFlush(persistenceEntity);
        updateVersion(aggregateRoot, persistenceEntity);
    }

    @SneakyThrows
    private void updateVersion(ShoppingCart aggregateRoot, ShoppingCartPersistenceEntity persistenceEntity) {
        Field version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);

        ReflectionUtils.setField(version, aggregateRoot, persistenceEntity.getVersion());

        version.setAccessible(false);
    }

    @Override
    public Long count() {
        return persistenceEntityRepository.count();
    }
}

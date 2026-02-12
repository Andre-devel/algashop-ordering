package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ShoppingCartPersistenceEntityAssembler {
    
    private final CustomerPersistenceEntityRepository customerRepository;
    
    public ShoppingCartPersistenceEntity fromDomain(ShoppingCart shoppingCart) {
        return merge(new ShoppingCartPersistenceEntity(), shoppingCart);  
    }

    public ShoppingCartPersistenceEntity merge(ShoppingCartPersistenceEntity shoppingCartPersistenceEntity, ShoppingCart shoppingCart) {
        shoppingCartPersistenceEntity.setId(shoppingCart.id().value());    
        shoppingCartPersistenceEntity.setTotalAmount(shoppingCart.totalAmount().value());
        shoppingCartPersistenceEntity.setTotalItems(shoppingCart.totalItems().value());

        CustomerPersistenceEntity customerReference = customerRepository.getReferenceById(shoppingCart.customerId().value());
        shoppingCartPersistenceEntity.setCustomer(customerReference);

        Set<ShoppingCartItemPersistenceEntity> mergeItems = mergeItems(shoppingCart, shoppingCartPersistenceEntity);
        shoppingCartPersistenceEntity.replaceItems(mergeItems);
        shoppingCartPersistenceEntity.setVersion(shoppingCart.version());
        
        shoppingCartPersistenceEntity.addEvent(shoppingCart.domainEvents());
        
        return shoppingCartPersistenceEntity;   
    }

    private Set<ShoppingCartItemPersistenceEntity> mergeItems(ShoppingCart shoppingCart, ShoppingCartPersistenceEntity shoppingCartPersistenceEntity) {
        Set<ShoppingCartItem> newOrUpdatedItems = shoppingCart.items();
        
        if (newOrUpdatedItems == null || newOrUpdatedItems.isEmpty()) {
            return new HashSet<>();
        }
        
        Set<ShoppingCartItemPersistenceEntity> existingItems = shoppingCartPersistenceEntity.getItems();

        if (existingItems == null || existingItems.isEmpty()) {
            return newOrUpdatedItems.stream().map(this::fromDomain).collect(Collectors.toSet());
        }

        Map<UUID, ShoppingCartItemPersistenceEntity> existingItemMap = existingItems.stream()
                .collect(Collectors.toMap(ShoppingCartItemPersistenceEntity::getId, item -> item));

        return newOrUpdatedItems.stream()
                .map(orderItem -> {
                    ShoppingCartItemPersistenceEntity itemsPersistence = existingItemMap
                            .getOrDefault(orderItem.id().value(), new ShoppingCartItemPersistenceEntity());
                    return merge(itemsPersistence, orderItem);
                }).collect(Collectors.toSet());
    }
    
    public ShoppingCartItemPersistenceEntity fromDomain(ShoppingCartItem item) {
        return merge(new ShoppingCartItemPersistenceEntity(), item);
    }
    
    public ShoppingCartItemPersistenceEntity merge(ShoppingCartItemPersistenceEntity itemPersistenceEntity, ShoppingCartItem item) {
        itemPersistenceEntity.setId(item.id().value());
        itemPersistenceEntity.setProductId(item.productId().value());   
        itemPersistenceEntity.setName(item.name().value());
        itemPersistenceEntity.setPrice(item.price().value());
        itemPersistenceEntity.setQuantity(item.quantity().value());
        itemPersistenceEntity.setTotalAmount(item.totalAmount().value());
        itemPersistenceEntity.setAvailable(item.isAvailable());
        
        return itemPersistenceEntity;
    }
}

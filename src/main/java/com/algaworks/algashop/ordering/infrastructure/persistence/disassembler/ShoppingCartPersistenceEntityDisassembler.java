package com.algaworks.algashop.ordering.infrastructure.persistence.disassembler;

import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCart;
import com.algaworks.algashop.ordering.domain.model.entity.ShoppingCartItem;
import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.ProductName;
import com.algaworks.algashop.ordering.domain.model.valueobject.Quantity;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartId;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ShoppingCartItemId;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistenceEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ShoppingCartPersistenceEntityDisassembler {
    
    public ShoppingCart toDomainEntity(ShoppingCartPersistenceEntity persistenceEntity) {
        return ShoppingCart.existing()
                .id(new ShoppingCartId(persistenceEntity.getId()))
                .customerId(new CustomerId(persistenceEntity.getCustomerId()))
                .totalAmount(new Money(persistenceEntity.getTotalAmount()))
                .totalItems(new Quantity(persistenceEntity.getTotalItems()))
                .createdAt(persistenceEntity.getCreatedAt())
                .items(new HashSet<>())
                .version(persistenceEntity.getVersion()) 
                .items(shoppingCartPersistenceEntityToItem(persistenceEntity.getItems()))  
                .build();
    }

    private Set<ShoppingCartItem> shoppingCartPersistenceEntityToItem(Set<ShoppingCartItemPersistenceEntity> items) {
        if (items == null || items.isEmpty()) {
            return new HashSet<>();
        }
        return items.stream().map(this::persistenceItemToItem).collect(Collectors.toSet());
    }

    private ShoppingCartItem persistenceItemToItem(ShoppingCartItemPersistenceEntity shoppingCartItemPersistenceEntity) {
        return ShoppingCartItem.existing()
                .id(new ShoppingCartItemId(shoppingCartItemPersistenceEntity.getId()))
                .productId(new ProductId(shoppingCartItemPersistenceEntity.getProductId()))
                .productName(new ProductName(shoppingCartItemPersistenceEntity.getProductName()))
                .price(new Money(shoppingCartItemPersistenceEntity.getPrice()))
                .quantity(new Quantity(shoppingCartItemPersistenceEntity.getQuantity()))    
                .totalAmount(new Money(shoppingCartItemPersistenceEntity.getTotalAmount()))
                .available(shoppingCartItemPersistenceEntity.getAvailable())    
                .shoppingCartId(new ShoppingCartId(shoppingCartItemPersistenceEntity.getShoppingCart().getId()))
                .build();
    }
}

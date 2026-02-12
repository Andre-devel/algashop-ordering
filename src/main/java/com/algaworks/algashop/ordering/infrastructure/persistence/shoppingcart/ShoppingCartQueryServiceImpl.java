package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.application.shoppingcart.management.ShoppingCartNotFoundException;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartItemOutput;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartOutput;
import com.algaworks.algashop.ordering.application.shoppingcart.query.ShoppingCartQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShoppingCartQueryServiceImpl implements ShoppingCartQueryService {
    
    private final ShoppingCartPersistenceEntityRepository shoppingCartRepository;
    
    @Override
    public ShoppingCartOutput findById(UUID shoppingCartId) {
        return shoppingCartRepository.findByIdWithItems(shoppingCartId).map(this::toOutput)
                    .orElseThrow(() -> new ShoppingCartNotFoundException("Shopping cart not found"));
    }

    @Override
    public ShoppingCartOutput findByCustomerId(UUID customerId) {
        return shoppingCartRepository.findByCustomerIdWithItems(customerId).map(this::toOutput)
                    .orElseThrow(() -> new ShoppingCartNotFoundException("Shopping cart not found for customer id: " + customerId));
    }

    private ShoppingCartOutput toOutput(ShoppingCartPersistenceEntity entity) {
        List<ShoppingCartItemOutput> items = entity.getItems().stream()
                .map(item -> new ShoppingCartItemOutput(
                        item.getId(),
                        item.getProductId(),
                        item.getName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getTotalAmount(),
                        item.getAvailable()
                ))
                .toList();

        return new ShoppingCartOutput(
                entity.getId(),
                entity.getCustomer().getId(),
                entity.getTotalItems(),
                entity.getTotalAmount(),
                items
        );
    }
}

package com.algaworks.algashop.ordering.domain.model.service;

import com.algaworks.algashop.ordering.domain.model.valueobject.Money;
import com.algaworks.algashop.ordering.domain.model.valueobject.id.ProductId;

public interface ShoppingCartProductAdjustmentService {
    
    void adjustPrices(ProductId productId, Money newPrice);
    void changeAvailability(ProductId productId, boolean available);
}

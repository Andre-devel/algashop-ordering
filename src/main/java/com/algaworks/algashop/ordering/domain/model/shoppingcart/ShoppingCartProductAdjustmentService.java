package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;

public interface ShoppingCartProductAdjustmentService {
    
    void adjustPrices(ProductId productId, Money newPrice);
    void changeAvailability(ProductId productId, boolean available);
}

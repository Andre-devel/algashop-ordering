package com.algaworks.algashop.ordering.domain.model.shoppingcart;

import com.algaworks.algashop.ordering.domain.model.AggregateRoot;
import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.Quantity;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerId;
import com.algaworks.algashop.ordering.domain.model.product.Product;
import com.algaworks.algashop.ordering.domain.model.product.ProductId;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ShoppingCart implements AggregateRoot<ShoppingCartId> {
    private ShoppingCartId id;
    private CustomerId customerId;
    
    private Money totalAmount;
    private Quantity totalItems;
    
    private OffsetDateTime createdAt;
    
    private Set<ShoppingCartItem> items;
    
    private Long version;

    @Builder(builderClassName = "ExistingShoppingCartBuilder", builderMethodName = "existing")
    public ShoppingCart(
            ShoppingCartId id,
            CustomerId customerId,
            Money totalAmount,
            Quantity totalItems,
            OffsetDateTime createdAt,
            Long version,
            Set<ShoppingCartItem> items
    ) {
        this.setId(id);
        this.setCustomerId(customerId);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.setCreatedAt(createdAt);
        this.setVersion(version);   
        this.setItems(items);
    };
    
    @Builder(builderClassName = "StartShoppingBuilder", builderMethodName = "brandNew") 
    public static ShoppingCart startShopping(CustomerId customerId) {
        return new ShoppingCart(
               new ShoppingCartId(),
               customerId,
               Money.ZERO,
               Quantity.ZERO,
               OffsetDateTime.now(),
                null,
               new HashSet<>()
        );
    }
    
    public void addItem(Product product, Quantity quantity) {
        Objects.requireNonNull(product);
        Objects.requireNonNull(quantity);

        product.checkOutOfStock();
        
        ShoppingCartItem newShoppingCartItem = ShoppingCartItem.brandNew()
                .shoppingCartId(this.id())
                .productId(product.id())
                .name(product.name()) 
                .quantity(quantity)
                .price(product.price())
                .available(product.inStock())
                .build();

        searchItemByProduct(product.id())
                .ifPresentOrElse(i -> updateItem(i, product, quantity), () -> insertItem(newShoppingCartItem));

        this.recalculateTotals();
    }

    public void refreshItem(Product product) {
        ShoppingCartItem shoppingCartItem = this.findItem(product.id());
        shoppingCartItem.refresh(product);
        this.recalculateTotals();
    }
    
    public void changeItemQuantity(ShoppingCartItemId shoppingCartItemId, Quantity quantity) {
        Objects.requireNonNull(shoppingCartItemId);
        Objects.requireNonNull(quantity);
        
        ShoppingCartItem shoppingCartItem = findShoppingCartItem(shoppingCartItemId);
        shoppingCartItem.changeQuantity(quantity);
        
        this.recalculateTotals();
    }
    
    public boolean containsUnavailableItems() {
        return this.items.stream().anyMatch(item -> !item.isAvailable());
    }

    public ShoppingCartItem findItem(ProductId productId) {
        Objects.requireNonNull(productId);
        return this.items.stream()
                .filter(i -> i.productId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ShoppingCartDoesNotContainProductException(this.id(), productId));
    }

    public ShoppingCartItem findItem(ShoppingCartItemId shoppingCartItemId) {
        Objects.requireNonNull(shoppingCartItemId);
        return this.items.stream()
                .filter(i -> i.id().equals(shoppingCartItemId))
                .findFirst()
                .orElseThrow(() -> new ShoppingCartDoesNotContainShoppingCartItemException(this.id(), shoppingCartItemId));
    }

    public void removeItem(ShoppingCartItemId shoppingCartItemId) {
        Objects.requireNonNull(shoppingCartItemId);

        ShoppingCartItem removeItem = findShoppingCartItem(shoppingCartItemId);
        this.items.remove(removeItem);
        
        recalculateTotals();
    }

    public void empty() {
        this.items.clear();
        
        recalculateTotals();
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public Long version() {
        return version;
    }

    public Quantity totalItems() {
        return totalItems;
    }

    public Money totalAmount() {
        return totalAmount;
    }

    public CustomerId customerId() {
        return customerId;
    }

    public ShoppingCartId id() {
        return id;
    }

    public Set<ShoppingCartItem> items() {
        return Collections.unmodifiableSet(this.items);
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    private void updateItem(ShoppingCartItem shoppingCartItem, Product product, Quantity quantity) {
        shoppingCartItem.refresh(product);
        shoppingCartItem.changeQuantity(shoppingCartItem.quantity().add(quantity));
    }

    private void insertItem(ShoppingCartItem shoppingCartItem) {
        this.items.add(shoppingCartItem);
    }

    private Optional<ShoppingCartItem> searchItemByProduct(ProductId productId) {
        Objects.requireNonNull(productId);
        return this.items.stream()
                .filter(i -> i.productId().equals(productId))
                .findFirst();
    }

    private ShoppingCartItem findShoppingCartItem(ShoppingCartItemId shoppingCartItemId) {
        Objects.requireNonNull(shoppingCartItemId);

        return this.items.stream()
                .filter(item -> item.id().equals(shoppingCartItemId))
                .findFirst()
                .orElseThrow(() -> new ShoppingCartDoesNotContainShoppingCartItemException(this.id, shoppingCartItemId));
    }

    private void recalculateTotals() {
        BigDecimal totalItemsAmount = this.items().stream().map(item -> item.totalAmount().value()).reduce(BigDecimal.ZERO, BigDecimal::add);
        Integer totalQuantity = this.items.stream().map(item -> item.quantity().value()).reduce(0, Integer::sum);

        this.setTotalAmount(new Money(totalItemsAmount));
        this.setTotalItems(new Quantity(totalQuantity));
    }

    private void setItems(Set<ShoppingCartItem> items) {
        Objects.requireNonNull(items);
        this.items = items;
    }

    private void setCreatedAt(OffsetDateTime createdAt) {
        Objects.requireNonNull(createdAt);
        this.createdAt = createdAt;
    }

    private void setVersion(Long version) {
        this.version = version;
    }

    private void setTotalItems(Quantity totalItems) {
        Objects.requireNonNull(totalItems);
        this.totalItems = totalItems;
    }

    private void setTotalAmount(Money totalAmount) {
        Objects.requireNonNull(totalAmount);
        this.totalAmount = totalAmount;
    }

    private void setCustomerId(CustomerId customerId) {
        Objects.requireNonNull(customerId);
        this.customerId = customerId;
    }

    private void setId(ShoppingCartId id) {
        Objects.requireNonNull(id);
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

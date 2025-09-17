package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;
@Entity
@Table(name = "shopping_cart_item") 
@Getter
@Setter
@ToString(of = "id")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCartItemPersistenceEntity {
    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    
    private UUID productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal totalAmount;
    private Boolean available;

    @JoinColumn
    @ManyToOne(optional = false)
    private ShoppingCartPersistenceEntity shoppingCart;
}

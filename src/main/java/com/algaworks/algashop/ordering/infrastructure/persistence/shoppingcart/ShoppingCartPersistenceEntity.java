package com.algaworks.algashop.ordering.infrastructure.persistence.shoppingcart;

import com.algaworks.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(of = "id")
@Table(name = "shopping_cart")  
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
public class ShoppingCartPersistenceEntity {
    @Id
    @EqualsAndHashCode.Include
    private UUID id;
    
    private BigDecimal totalAmount; 
    private Integer totalItems;

    @JoinColumn
    @ManyToOne(optional = false)
    private CustomerPersistenceEntity customer;
    
    @OneToMany(mappedBy = "shoppingCart",cascade = CascadeType.ALL)
    private Set<ShoppingCartItemPersistenceEntity> items = new HashSet<>();     

    @CreatedBy
    private UUID createdByUserId;

    @CreatedDate
    private OffsetDateTime createdAt;

    @LastModifiedDate
    private OffsetDateTime lastModifiedAt;

    @LastModifiedBy
    private UUID lastModifiedByUserId;

    @Version
    private Long version;
    
    @Builder
    private ShoppingCartPersistenceEntity(
            UUID id,
            CustomerPersistenceEntity customer,
            BigDecimal totalAmount,
            Integer totalItems,
            Set<ShoppingCartItemPersistenceEntity> items
    ) {
        this.setId(id);
        this.setCustomer(customer);
        this.setTotalAmount(totalAmount);
        this.setTotalItems(totalItems);
        this.replaceItems(items);
    }   

    public void replaceItems(Set<ShoppingCartItemPersistenceEntity> items) {
        if (items == null || items.isEmpty()) {
            this.setItems((new HashSet<>()));
            return;
        }

        items.forEach(i -> i.setShoppingCart(this));
        this.setItems(items);
    }

    public void addItem(ShoppingCartItemPersistenceEntity item) {
        if (item == null) {
            return;
        }

        if (this.getItems() == null) {
            this.setItems((new HashSet<>()));
        }

        item.setShoppingCart(this);
        this.getItems().add(item);
    }
    
    public UUID getCustomerId() {
        if (this.getCustomer() == null) {
            return null;
        }
        return this.getCustomer().getId();
    }   
}

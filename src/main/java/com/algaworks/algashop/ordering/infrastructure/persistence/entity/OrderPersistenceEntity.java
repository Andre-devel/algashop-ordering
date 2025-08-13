package com.algaworks.algashop.ordering.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(of = "id")
@Table(name = "\"order\"")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class OrderPersistenceEntity {
    
    @Id
    @EqualsAndHashCode.Include
    private Long id;
    private UUID customerId;
    
    private BigDecimal totalAmount;
    private Integer totalItems;
    private String status;
    private String paymentMethod;
    
    private OffsetDateTime placeAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime cancelAt;
    private OffsetDateTime readyAt;
}

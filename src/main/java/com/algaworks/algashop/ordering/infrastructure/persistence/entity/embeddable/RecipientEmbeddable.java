package com.algaworks.algashop.ordering.infrastructure.persistence.entity.embeddable;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RecipientEmbeddable {
    private String firstName;
    private String lastName;
    private String document;
    private String phone;
}

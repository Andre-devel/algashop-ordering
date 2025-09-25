package com.algaworks.algashop.ordering.domain.model.order.shipping;

import com.algaworks.algashop.ordering.domain.model.commons.Money;
import com.algaworks.algashop.ordering.domain.model.commons.ZipCode;
import lombok.Builder;

import java.time.LocalDate;

public interface ShippingCostService {

    CalculationResult calculate(CalculationRequest calculationRequest);
    
    @Builder    
    record CalculationResult(Money cost, LocalDate expectedDate) {
    }
    
    @Builder
    record CalculationRequest(ZipCode origin, ZipCode destination) {
    }
}

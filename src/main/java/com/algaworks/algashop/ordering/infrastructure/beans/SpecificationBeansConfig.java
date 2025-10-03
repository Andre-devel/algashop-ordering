package com.algaworks.algashop.ordering.infrastructure.beans;

import com.algaworks.algashop.ordering.domain.model.order.CustomerHaveFreeShippingSpecification;
import com.algaworks.algashop.ordering.domain.model.order.Orders;
import org.springframework.context.annotation.Bean;

public class SpecificationBeansConfig {
    
    @Bean
    public CustomerHaveFreeShippingSpecification customerHaveFreeShippingSpecification(Orders order) {
        return new CustomerHaveFreeShippingSpecification(order, 200, 2L, 2000);
    }
}

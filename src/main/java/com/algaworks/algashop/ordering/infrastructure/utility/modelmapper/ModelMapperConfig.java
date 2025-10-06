package com.algaworks.algashop.ordering.infrastructure.utility.modelmapper;

import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.application.order.query.OrderDetailOutput;
import com.algaworks.algashop.ordering.application.order.query.OrderItemDetailOutput;
import com.algaworks.algashop.ordering.application.utility.Mapper;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.customer.BirthDate;
import com.algaworks.algashop.ordering.domain.model.customer.Customer;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderItemPersistenceEntity;
import com.algaworks.algashop.ordering.infrastructure.persistence.order.OrderPersistenceEntity;
import io.hypersistence.tsid.TSID;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.convention.NamingConventions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class ModelMapperConfig {
    
    private static final Converter<FullName, String> fullNameToFistNameConverter = mappingContext -> {
        FullName fullName = mappingContext.getSource();
        
        if (fullName == null) {
            return null;
        }
        
        return fullName.firstName();
    };

    private static final Converter<FullName, String> fullNameToLastNameConverter = mappingContext -> {
        FullName fullName = mappingContext.getSource();

        if (fullName == null) {
            return null;
        }

        return fullName.lastName();
    };

    private static final Converter<BirthDate, LocalDate> birthDateToLocalDateConverter = mappingContext -> {
        BirthDate birthDate = mappingContext.getSource();
        if (birthDate == null) {
            return null;
        }

        return birthDate.value();
    };
    
    private static final Converter<Long, String> longToStringConverter = mappingContext -> {
        Long tsidAsLong = mappingContext.getSource();
        if (tsidAsLong == null) {
            return null;
        }
        
        return new TSID(tsidAsLong).toString();
    };
    
    @Bean
    public Mapper mapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        configuration(modelMapper);
        
        return new Mapper() {
            @Override
            public <T> T convert(Object object, Class<T> destinationClass) {
                return modelMapper.map(object, destinationClass);
            }
        };
    }

    private void configuration(ModelMapper modelMapper) {
        modelMapper.getConfiguration()
                .setSourceNamingConvention(NamingConventions.NONE)
                .setDestinationNamingConvention(NamingConventions.NONE)
                .setMatchingStrategy(MatchingStrategies.STRICT);
        
        modelMapper.createTypeMap(Customer.class, CustomerOutput.class)
                .addMappings(mapping -> 
                        mapping.using(fullNameToFistNameConverter)
                                .map(Customer::fullName, CustomerOutput::setFirstName)
                )
                .addMappings(mapping ->
                        mapping.using(fullNameToLastNameConverter)
                                .map(Customer::fullName, CustomerOutput::setLastName)
                )
                .addMappings(mapping ->
                        mapping.using(birthDateToLocalDateConverter)
                                .map(Customer::birthDate, CustomerOutput::setBirthDate)
                );
        
        modelMapper.createTypeMap(OrderPersistenceEntity.class, OrderDetailOutput.class)
                .addMappings(mapping ->
                        mapping.using(longToStringConverter)
                                .map(OrderPersistenceEntity::getId, OrderDetailOutput::setId));

        modelMapper.createTypeMap(OrderItemPersistenceEntity.class, OrderItemDetailOutput.class)
                .addMappings(mapping ->
                        mapping.using(longToStringConverter)
                                .map(OrderItemPersistenceEntity::getId, OrderItemDetailOutput::setId))
                .addMappings(mapping ->
                        mapping.using(longToStringConverter)
                                .map(OrderItemPersistenceEntity::getOrderId, OrderItemDetailOutput::setOrderId));
    }
}

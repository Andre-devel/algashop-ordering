package com.algaworks.algashop.ordering.aplication.utility;

public interface Mapper {
    <T> T convert(Object object, Class<T> destinationClass);
}

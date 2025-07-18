package com.algaworks.algashop.ordering.domain.validator;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

public class FieldValidations {
    private FieldValidations() {}
    
    public static void requireValidEmail(String email) {
       requireValidEmail(email, null);
    }

    public static void requireValidEmail(String email, String errorMessage) {
        Objects.requireNonNull(email, errorMessage);
        
        if (email.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
        
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
    
    public static void requireNotBlank(String value) {
        requireNotBlank(value, "");
    }

    public static void requireNotBlank(String value, String errorMessage) {
        Objects.requireNonNull(value, errorMessage);

        if (value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
    
}

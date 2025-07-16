package com.algaworks.algashop.ordering.domain.entity;

import com.algaworks.algashop.ordering.domain.exception.CustomerArchivedException;
import static com.algaworks.algashop.ordering.domain.exception.ErrorMessage.VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST;
import static com.algaworks.algashop.ordering.domain.exception.ErrorMessage.VALIDATION_ERROR_EMAIL_IS_INVALID;
import static com.algaworks.algashop.ordering.domain.exception.ErrorMessage.VALIDATION_ERROR_FULLNAME_IS_BLANK;
import static com.algaworks.algashop.ordering.domain.exception.ErrorMessage.VALIDATION_ERROR_FULLNAME_IS_NULL;
import com.algaworks.algashop.ordering.domain.validator.FieldValidations;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Customer {
    private UUID id;
    private String fullName;
    private LocalDate birthDate;
    private String email;
    private String phone;
    private String document;
    private boolean promotionNotificationsAllowed;
    private boolean archived;
    private OffsetDateTime registeredAt;
    private OffsetDateTime archivedAt;
    private Integer loyaltyPoints;

    public Customer(
            UUID id,
            String fullName,
            LocalDate birthDate,
            String email,
            String phone,
            String document,
            boolean promotionNotificationsAllowed,
            OffsetDateTime registeredAt
    ) {
        this.setId(id);
        this.setFullName(fullName);
        this.setBirthDate(birthDate);
        this.setEmail(email);
        this.setPhone(phone);
        this.setDocument(document);
        this.setPromotionNotificationsAllowed(promotionNotificationsAllowed);
        this.setArchived(false);
        this.setRegisteredAt(registeredAt);
        this.setLoyaltyPoints(0);
    }

    public Customer(
            UUID id,
            String fullName,
            LocalDate birthDate,
            String email,
            String phone,
            String document,
            boolean promotionNotificationsAllowed,
            boolean archived,
            OffsetDateTime registeredAt,
            OffsetDateTime archivedAt,
            Integer loyaltyPoints
    ) {
        this.setId(id);
        this.setFullName(fullName);
        this.setBirthDate(birthDate);
        this.setEmail(email);
        this.setPhone(phone);
        this.setDocument(document);
        this.setPromotionNotificationsAllowed(promotionNotificationsAllowed);
        this.setArchived(archived);
        this.setRegisteredAt(registeredAt);
        this.setArchivedAt(archivedAt);
        this.setLoyaltyPoints(loyaltyPoints);
    }
    
    public void addLoyaltyPoints(int loyaltyPointsAdded) {
        verifyIfChangeable();
        
        if (loyaltyPointsAdded <= 0) {
            throw new IllegalArgumentException();
        }
        
        this.setLoyaltyPoints(this.loyaltyPoints + loyaltyPointsAdded);
    }
    
    public void archive() {
        verifyIfChangeable();

        this.setArchived(true);
        this.setArchivedAt(OffsetDateTime.now());
        this.setFullName("Anonymous");
        this.setPhone("000-000-0000");
        this.setDocument("000-00-0000");
        this.setEmail(UUID.randomUUID() + "@anonymous.com");
        this.setPromotionNotificationsAllowed(false);
        this.setBirthDate(null);
    }

    public void enablePromotionNotifications() {
        verifyIfChangeable();
        this.promotionNotificationsAllowed = true;
    }
    
    public void disablePromotionNotifications() {
        verifyIfChangeable();
        this.promotionNotificationsAllowed = false;
    }
    
    public void changeName(String fullName) {
        verifyIfChangeable();
        this.setFullName(fullName);
    }
    
    public void changeEmail(String email) {
        verifyIfChangeable();
        this.setEmail(email);
    }
    
    public void changePhone(String phone) {
        verifyIfChangeable();
        this.setPhone(phone);
    }

    public boolean isArchived() {
        return archived;
    }

    public UUID id() {
        return id;
    }

    public String fullName() {
        return fullName;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    public String email() {
        return email;
    }

    public String phone() {
        return phone;
    }

    public String document() {
        return document;
    }

    public boolean isPromotionNotificationsAllowed() {
        return promotionNotificationsAllowed;
    }

    public OffsetDateTime registeredAt() {
        return registeredAt;
    }

    public OffsetDateTime archivedAt() {
        return archivedAt;
    }

    public Integer loyaltyPoints() {
        return loyaltyPoints;
    }

    private void setId(UUID id) {
        Objects.requireNonNull(id);
        
        this.id = id;
    }

    private void setFullName(String fullName) {
        Objects.requireNonNull(fullName, VALIDATION_ERROR_FULLNAME_IS_NULL);
        
        if (fullName.isBlank()) {
            throw new IllegalArgumentException(VALIDATION_ERROR_FULLNAME_IS_BLANK);
        }
        
        this.fullName = fullName;
    }

    private void setBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            this.birthDate = null;
            return;
        }
        
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST);
        }
        
        this.birthDate = birthDate;
    }

    private void setEmail(String email) {
        FieldValidations.requireValidEmail(email, VALIDATION_ERROR_EMAIL_IS_INVALID);
        this.email = email;
    }

    private void setPhone(String phone) {
        Objects.requireNonNull(phone);
        this.phone = phone;
    }

    private void setDocument(String document) {
        Objects.requireNonNull(document);
        this.document = document;
    }

    private void setPromotionNotificationsAllowed(boolean promotionNotificationsAllowed) {
        this.promotionNotificationsAllowed = promotionNotificationsAllowed;
    }

    private void setArchived(boolean archived) {
        this.archived = archived;
    }

    private void setRegisteredAt(OffsetDateTime registeredAt) {
        Objects.requireNonNull(registeredAt);
        this.registeredAt = registeredAt;
    }

    private void setArchivedAt(OffsetDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    private void setLoyaltyPoints(Integer loyaltyPoints) {
        Objects.requireNonNull(loyaltyPoints);
        
        if (loyaltyPoints < 0) {
            throw new IllegalArgumentException("Loyalty points cannot be negative");
        }
        
        this.loyaltyPoints = loyaltyPoints;
    }

    private void verifyIfChangeable() {
        if (this.isArchived()) {
            throw new CustomerArchivedException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

package com.algaworks.algashop.ordering.infrastructure.persistence.customer;

import com.algaworks.algashop.ordering.application.customer.query.CustomerFilter;
import com.algaworks.algashop.ordering.application.customer.query.CustomerOutput;
import com.algaworks.algashop.ordering.application.customer.query.CustomerQueryService;
import com.algaworks.algashop.ordering.application.customer.query.CustomerSummaryOutput;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerQueryServiceImpl implements CustomerQueryService {
    
    private final EntityManager entityManager;

    private static final String findByIdAsOutputJPQL = """
            SELECT new com.algaworks.algashop.ordering.application.customer.query.CustomerOutput(
                c.id,
                c.firstName,
                c.lastName,
                c.email,
                c.document,
                c.phone,
                c.birthDate,
                c.loyaltyPoints,
                c.registeredAt,
                c.archivedAt,
                c.promotionNotificationsAllowed,
                c.archived,
                new com.algaworks.algashop.ordering.application.commons.AddressData(
                    c.address.street,
                    c.address.number,
                    c.address.complement,
                    c.address.neighborhood,
                    c.address.city,
                    c.address.state,
                    c.address.zipCode
                )
            )
            FROM CustomerPersistenceEntity c
            WHERE c.id = :id""";
    
    @Override
    public CustomerOutput findById(UUID customerId) {
        try {
            TypedQuery<CustomerOutput> query = entityManager.createQuery(findByIdAsOutputJPQL, CustomerOutput.class);
            query.setParameter("id", customerId);
            
            return  query.getSingleResult();
        } catch (NoResultException e) {
            throw new CustomerNotFoundException("Customer not found");
        }
    }

    @Override
    public Page<CustomerSummaryOutput> filter(CustomerFilter filter) {
        Long totalQueryResult = contTotalQueryResult(filter);
        
        if (totalQueryResult.equals(0L)) {
            PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
            new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResult);
        }
        
        return filterQuery(filter, totalQueryResult);
    }

    private Page<CustomerSummaryOutput> filterQuery(CustomerFilter filter, Long totalQueryResult) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomerSummaryOutput> criteriaQuery = criteriaBuilder.createQuery(CustomerSummaryOutput.class);
        
        Root<CustomerPersistenceEntity> root = criteriaQuery.from(CustomerPersistenceEntity.class);
        criteriaQuery.select(criteriaBuilder.construct(CustomerSummaryOutput.class,
                root.get("id"),
                root.get("firstName"),
                root.get("lastName"),
                root.get("email"),
                root.get("document"),
                root.get("phone"),
                root.get("birthDate"),
                root.get("loyaltyPoints"),
                root.get("registeredAt"),
                root.get("archivedAt"),
                root.get("promotionNotificationsAllowed"),
                root.get("archived")
        ));
        
        Predicate[] predicates = toPredicates(criteriaBuilder, root, filter);
        Order sortOrder = toSortOrder(criteriaBuilder, root, filter);
        
        criteriaQuery.where(predicates);

        if (sortOrder != null) {
            criteriaQuery.orderBy(sortOrder);
        }

        TypedQuery<CustomerSummaryOutput> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(filter.getPage() * filter.getSize());
        query.setMaxResults(filter.getSize());

        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());

        return new PageImpl<>(query.getResultList(), pageRequest, totalQueryResult);
    }

    private Order toSortOrder(CriteriaBuilder criteriaBuilder, Root<CustomerPersistenceEntity> root, CustomerFilter filter) {
        if (filter.getSortDirectionOrDefault() == Sort.Direction.ASC) {
            return criteriaBuilder.asc(root.get(filter.getSortByPropertyOrDefault().getPropertyName()));
        }

        if (filter.getSortDirectionOrDefault() == Sort.Direction.DESC) {
            return criteriaBuilder.desc(root.get(filter.getSortByPropertyOrDefault().getPropertyName()));
        }
        
        return null;
    }

    private Long contTotalQueryResult(CustomerFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaBuilderQuery = criteriaBuilder.createQuery(Long.class);
        Root<CustomerPersistenceEntity> root = criteriaBuilderQuery.from(CustomerPersistenceEntity.class);

        Expression<Long> selection = criteriaBuilder.count(root);
        Predicate[] predicates = toPredicates(criteriaBuilder, root, filter);

        criteriaBuilderQuery.select(selection);
        criteriaBuilderQuery.where(predicates);

        TypedQuery<Long> query = entityManager.createQuery(criteriaBuilderQuery);
        
        return query.getSingleResult();
    }

    private Predicate[] toPredicates(CriteriaBuilder criteriaBuilder, Root<CustomerPersistenceEntity> root, CustomerFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        
        if (filter.getFirstName() != null && !filter.getFirstName().isBlank()) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("firstName")),
                    "%" + filter.getFirstName().toLowerCase() + "%"
            ));
        }
        
        if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
            predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")),
                    "%" + filter.getEmail().toLowerCase() + "%"
            ));
        }
        
        return predicates.toArray(new Predicate[] {});
    }
}

package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.application.order.query.CustomerMinimalOutput;
import com.algaworks.algashop.ordering.application.order.query.OrderDetailOutput;
import com.algaworks.algashop.ordering.application.order.query.OrderFilter;
import com.algaworks.algashop.ordering.application.order.query.OrderQueryService;
import com.algaworks.algashop.ordering.application.order.query.OrderSummaryOutput;
import com.algaworks.algashop.ordering.application.utility.Mapper;
import com.algaworks.algashop.ordering.domain.model.customer.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
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

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryServiceImpl implements OrderQueryService {
    
    private final OrderPersistenceEntityRepository repository;
    private final Mapper mapper;
    
    private final EntityManager entityManager;
    
    @Override
    public OrderDetailOutput findById(String id) {
        OrderPersistenceEntity entity = repository.findById(new OrderId(id).value().toLong())
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + id));

        return mapper.convert(entity, OrderDetailOutput.class);
    }

    @Override
    public Page<OrderSummaryOutput> filter(OrderFilter filter) {
        Long totalQueryResult = contTotalQueryResult(filter);
        
        if (totalQueryResult.equals(0L)) {
            PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
            new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResult);
        }
        
        return filterQuery(filter, totalQueryResult);
    }

    private Long contTotalQueryResult(OrderFilter filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaBuilderQuery = criteriaBuilder.createQuery(Long.class);
        Root<OrderPersistenceEntity> root = criteriaBuilderQuery.from(OrderPersistenceEntity.class);

        Expression<Long> selection = criteriaBuilder.count(root);
        Predicate[] predicates = toPredicates(criteriaBuilder, root, filter);
        
        criteriaBuilderQuery.select(selection);
        criteriaBuilderQuery.where(predicates);

        TypedQuery<Long> query = entityManager.createQuery(criteriaBuilderQuery);
        
        return query.getSingleResult();
    }
    
    private Page<OrderSummaryOutput> filterQuery(OrderFilter filter, Long totalQueryResult) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderSummaryOutput> criteriaQuery = criteriaBuilder.createQuery(OrderSummaryOutput.class);
        
        Root<OrderPersistenceEntity> root = criteriaQuery.from(OrderPersistenceEntity.class);
        Path<Object> customer = root.get("customer");


        criteriaQuery.select(criteriaBuilder.construct(OrderSummaryOutput.class,
                root.get("id"),
                root.get("totalItems"),
                root.get("totalAmount"),
                root.get("placedAt"),
                root.get("paidAt"),
                root.get("canceledAt"),
                root.get("readyAt"),
                root.get("status"),
                root.get("paymentMethod"),
                criteriaBuilder.construct(CustomerMinimalOutput.class,
                        customer.get("id"),
                        customer.get("firstName"),
                        customer.get("lastName"),
                        customer.get("email"),
                        customer.get("document"),
                        customer.get("phone")
                )
        ));

        Predicate[] predicates = toPredicates(criteriaBuilder, root, filter);
        Order sortOrder = toSortOrder(criteriaBuilder, root, filter);
        
        criteriaQuery.where(predicates);
        
        if (sortOrder != null) {
            criteriaQuery.orderBy(sortOrder);
        }
        
        TypedQuery<OrderSummaryOutput> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(filter.getPage() * filter.getSize());
        query.setMaxResults(filter.getSize());
        
        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
        
        return new PageImpl<>(query.getResultList(), pageRequest, totalQueryResult);
    }

    private Order toSortOrder(CriteriaBuilder criteriaBuilder, Root<OrderPersistenceEntity> root, OrderFilter filter) {
        if (filter.getSortDirectionOrDefault() == Sort.Direction.ASC) {
            return criteriaBuilder.asc(root.get(filter.getSortByPropertyOrDefault().getPropertyName()));
        }
        
        if (filter.getSortDirectionOrDefault() == Sort.Direction.DESC) {
            return criteriaBuilder.desc(root.get(filter.getSortByPropertyOrDefault().getPropertyName()));
        }
        
        return null;
    }

    private Predicate[] toPredicates(CriteriaBuilder builder, Root<OrderPersistenceEntity> root, OrderFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        
        if (filter.getCustomerId() != null) {
            predicates.add(builder.equal(root.get("customer").get("id"), filter.getCustomerId()));
        }
        
        if (filter.getStatus() != null && !filter.getStatus().isBlank()) {
            predicates.add(builder.equal(root.get("status"), filter.getStatus().toUpperCase()));
        }
        
        if (filter.getOrderId() != null && !filter.getOrderId().isBlank()) {
            long ordeIdLongValue;
            try {
                OrderId orderId = new OrderId(filter.getOrderId());
                ordeIdLongValue = orderId.value().toLong();
            } catch (IllegalArgumentException e) {
                ordeIdLongValue = 0L;
            }
            
            predicates.add(builder.equal(root.get("id"), ordeIdLongValue));
        }
        
        if (filter.getPlacedAtFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("placedAt"), filter.getPlacedAtFrom()));
        }

        if (filter.getPlacedAtTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("placedAt"), filter.getPlacedAtTo()));
        }

        if (filter.getTotalAmountFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("totalAmount"), filter.getTotalAmountFrom()));
        }

        if (filter.getTotalAmountTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("totalAmount"), filter.getTotalAmountTo()));
        }
        
        return predicates.toArray(new Predicate[] {});
    }

}

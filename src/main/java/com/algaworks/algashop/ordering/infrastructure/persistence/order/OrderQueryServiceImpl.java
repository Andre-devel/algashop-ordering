package com.algaworks.algashop.ordering.infrastructure.persistence.order;

import com.algaworks.algashop.ordering.application.order.query.CustomerMinimalOutput;
import com.algaworks.algashop.ordering.application.order.query.OrderDetailOutput;
import com.algaworks.algashop.ordering.application.order.query.OrderQueryService;
import com.algaworks.algashop.ordering.application.order.query.OrderSummaryOutput;
import com.algaworks.algashop.ordering.application.utility.Mapper;
import com.algaworks.algashop.ordering.application.utility.PageFilter;
import com.algaworks.algashop.ordering.domain.model.customer.OrderNotFoundException;
import com.algaworks.algashop.ordering.domain.model.order.OrderId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

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
    public Page<OrderSummaryOutput> filter(PageFilter filter) {
        Long totalQueryResult = contTotalQueryResult();
        
        if (totalQueryResult.equals(0L)) {
            PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
            new PageImpl<>(new ArrayList<>(), pageRequest, totalQueryResult);
        }
        
        return filterQuery(filter, totalQueryResult);
    }

    private Long contTotalQueryResult() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaBuilderQuery = criteriaBuilder.createQuery(Long.class);
        Root<OrderPersistenceEntity> root = criteriaBuilderQuery.from(OrderPersistenceEntity.class);

        Expression<Long> selection = criteriaBuilder.count(root);
        criteriaBuilderQuery.select(selection);

        TypedQuery<Long> query = entityManager.createQuery(criteriaBuilderQuery);
        
        return query.getSingleResult();
    }
    
    private Page<OrderSummaryOutput> filterQuery(PageFilter filter, Long totalQueryResult) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<OrderSummaryOutput> criteriaQuery = builder.createQuery(OrderSummaryOutput.class);
        
        Root<OrderPersistenceEntity> root = criteriaQuery.from(OrderPersistenceEntity.class);
        Path<Object> customer = root.get("customer");


        criteriaQuery.select(builder.construct(OrderSummaryOutput.class,
                root.get("id"),
                builder.construct(CustomerMinimalOutput.class, 
                        customer.get("id"),
                        customer.get("firstName"),
                        customer.get("lastName"),
                        customer.get("email"),
                        customer.get("document"),
                        customer.get("phone")
                ),
                root.get("totalItems"),
                root.get("totalAmount"),
                root.get("placedAt"),
                root.get("paidAt"),
                root.get("cancelAt"),
                root.get("readyAt"),
                root.get("status"),
                root.get("paymentMethod")
        ));

        TypedQuery<OrderSummaryOutput> query = entityManager.createQuery(criteriaQuery);
        query.setFirstResult(filter.getPage() * filter.getSize());
        query.setMaxResults(filter.getSize());
        
        PageRequest pageRequest = PageRequest.of(filter.getPage(), filter.getSize());
        
        return new PageImpl<>(query.getResultList(), pageRequest, totalQueryResult);
    }

}

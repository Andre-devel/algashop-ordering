package com.algaworks.algashop.ordering.application.customer.query;

import com.algaworks.algashop.ordering.domain.model.commons.Email;
import com.algaworks.algashop.ordering.domain.model.commons.FullName;
import com.algaworks.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.algaworks.algashop.ordering.domain.model.customer.Customers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
//@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
//@Sql(scripts = "classpath:sql/clean-database.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@Transactional
class CustomerQueryServiceIT {
    
    @Autowired
    private CustomerQueryService customerQueryService;
    
    @Autowired
    private Customers customers;
    
    @Test
    public void shouldFindByEmail() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().email(new Email("erick.us@hotmail.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().email(new Email("jorge@hotmail.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().email(new Email("luana@hotmail.com")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setEmail("erick");
        
        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(1);
    }
    
    @Test
    public void shouldFindByFirstName() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("ERICK", "us")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("erick", "Silva")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Luana", "Silva")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setFirstName("eri");
        
        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(2);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(2);
    }
    
    @Test
    public void shouldFindByFirstNameAndEmail() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("ERICK", "us")).email(new Email("erick.us@hotmail.com")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("erick", "Silva")).email(new Email("jardin@hotmail.com")).build());

        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Luana", "Silva")).email(new Email("luana.silva@hotmail.com")).build());

        CustomerFilter filter = new CustomerFilter();
        filter.setFirstName("eri");
        filter.setEmail("jardin");

        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);
        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(1);
    }
    
    @Test
    public void shouldReturnExactlyOnePageWhenThereAreMoreThanOnePage() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());

        CustomerFilter filter = new CustomerFilter(5, 0);
        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(8);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(5);
    }
    
    @Test
    public void shouldOrderByFirstNameDescending() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Ana", "Silva")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Bruno", "Silva")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Carlos", "Silva")).build());

        CustomerFilter filter = new CustomerFilter(5, 0);
        filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        filter.setSortDirection(Sort.Direction.DESC);
        
        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(3);
        
        Assertions.assertThat(page.getContent().get(0).getFirstName()).isEqualTo("Carlos");
        Assertions.assertThat(page.getContent().get(1).getFirstName()).isEqualTo("Bruno");
        Assertions.assertThat(page.getContent().get(2).getFirstName()).isEqualTo("Ana");
    }
    
    @Test
    public void shouldOrderByFirstNameAscending() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Ana", "Silva")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Bruno", "Silva")).build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().fullName(new FullName("Carlos", "Silva")).build());

        CustomerFilter filter = new CustomerFilter(5, 0);
        filter.setSortByProperty(CustomerFilter.SortType.FIRST_NAME);
        filter.setSortDirection(Sort.Direction.ASC);

        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(3);

        Assertions.assertThat(page.getContent().get(0).getFirstName()).isEqualTo("Ana");
        Assertions.assertThat(page.getContent().get(1).getFirstName()).isEqualTo("Bruno");
        Assertions.assertThat(page.getContent().get(2).getFirstName()).isEqualTo("Carlos");
    }
    
    @Test
    public void shouldOrderByRegisteredAtDescending() throws InterruptedException {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        Thread.sleep(10);
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        Thread.sleep(10);
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());

        CustomerFilter filter = new CustomerFilter(5, 0);
        filter.setSortByProperty(CustomerFilter.SortType.REGISTERED_AT);
        filter.setSortDirection(Sort.Direction.DESC);

        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(3);

        Assertions.assertThat(page.getContent().get(0).getRegisteredAt()).isAfter(page.getContent().get(1).getRegisteredAt());
        Assertions.assertThat(page.getContent().get(1).getRegisteredAt()).isAfter(page.getContent().get(2).getRegisteredAt());
    }
    
    @Test
    public void shouldOrderByRegisteredAtAscending() throws InterruptedException {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        Thread.sleep(10);
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        Thread.sleep(10);
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());

        CustomerFilter filter = new CustomerFilter(5, 0);
        filter.setSortByProperty(CustomerFilter.SortType.REGISTERED_AT);
        filter.setSortDirection(Sort.Direction.ASC);

        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(1);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(3);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(3);

        Assertions.assertThat(page.getContent().get(0).getRegisteredAt()).isBefore(page.getContent().get(1).getRegisteredAt());
        Assertions.assertThat(page.getContent().get(1).getRegisteredAt()).isBefore(page.getContent().get(2).getRegisteredAt());
    }
    
    @Test
    public void shouldReturnEmptyWhenThereIsNoCustomer() {
        CustomerFilter filter = new CustomerFilter(5, 0);
        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(0);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(0);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(0);
    }
    
    @Test
    public void shouldReturnEmptyWhenThereIsNoCustomerMatchingTheFilter() {
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());
        customers.add(CustomerTestDataBuilder.brandNewCustomer().build());

        CustomerFilter filter = new CustomerFilter();
        filter.setFirstName("non-existing-first-name");
        
        Page<CustomerSummaryOutput> page = customerQueryService.filter(filter);

        Assertions.assertThat(page.getTotalPages()).isEqualTo(0);
        Assertions.assertThat(page.getTotalElements()).isEqualTo(0);
        Assertions.assertThat(page.getNumberOfElements()).isEqualTo(0);
    }
}
package com.example.customer.domain.service;

import com.example.borrowing.exception.BorrowingConflictException;
import com.example.customer.controller.dto.CustomerAndCountDto;
import com.example.customer.domain.entity.Customer;
import com.example.customer.domain.entity.CustomerFilter;
import com.example.customer.domain.repository.CustomerRepository;
import com.example.customer.exception.CustomerNotFoundException;
import com.example.request.ExtendedRequest;
import com.example.utils.CalculateIndex.CalculateIndex;
import com.example.utils.CalculateIndex.Index;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class CustomerPersistenceService {

    @Inject
    CustomerRepository repository;

    @Transactional
    public Customer persist(Customer customer) {
        log.debug("persist: {}", customer);

        repository.persist(customer);
        return customer;
    }

    public CustomerAndCountDto getAll(ExtendedRequest request) {
        log.debug("getAll");

        List<Customer> sublist;
        CalculateIndex calculateIndex = new CalculateIndex();
        List<Customer> customers = request.getSortable().isAscending() ? repository.listAll(
                Sort.by(request.getSortable().getColumn()).ascending())
                : repository.listAll(Sort.by(request.getSortable().getColumn()).descending());
        Index indexes = calculateIndex.calculateIndex(request, customers.size());
        sublist = customers.subList(indexes.getFromIndex(), indexes.getToIndex());
        return new CustomerAndCountDto(customers.size(), sublist);
    }

    public List<Customer> getAll() {
        log.debug("getAll");

        return repository.listAll(Sort.by("id").ascending());
    }

    public List<Customer> getAllByFirstName(String firstName) {
        log.debug("getAllByFirstName: {}", firstName);
        firstName = "%" + firstName.toLowerCase() + "%";

        return repository.list("Select e from Customer e where lower(e.firstName) like ?1", firstName);
    }

    public List<Customer> getAllByLastName(String lastName) {
        log.debug("getAllByLastName: {}", lastName);
        lastName = "%" + lastName.toLowerCase() + "%";

        return repository.list("Select e from Customer e where lower(e.lastName) like ?1", lastName);
    }

    public List<Customer> getAllByFirstNameAndLastName(String firstName, String lastName) {
        log.debug("getAllByFirstNameAndLastName: {} {}", firstName, lastName);
        String first = "%" + firstName.toLowerCase() + "%";
        String last = "%" + lastName.toLowerCase() + "%";

        return repository.list(
                "Select e from Customer e where lower(e.firstName) like ?1 and lower(e.lastName) like ?2", first,
                last);
    }

    public Customer getCustomerById(Integer id) throws CustomerNotFoundException {
        log.debug("getCustomerById: {}", id);

        Customer customer = repository.findById(Long.valueOf(id));
        if (customer != null) {
            return customer;
        }
        throw new CustomerNotFoundException(String.format("Zakaznik s ID = %s nebol najdeny", id));
    }

    @Transactional
    public Customer updateCustomer(Integer id, Customer update) throws CustomerNotFoundException {
        log.debug("updateCustomer: {} {}", id, update);

        Customer customer = getCustomerById(id);
        customer.setFirstName(update.getFirstName());
        customer.setLastName(update.getLastName());
        customer.setEmail(update.getEmail());
        return repository.getEntityManager().merge(customer);
    }

    @Transactional
    public void deleteCustomer(Integer id) throws CustomerNotFoundException, BorrowingConflictException {
        log.debug("deleteCustomer:{}", id);

        Customer customer = getCustomerById(id);
        if (!customer.getBorrowings().isEmpty()) {
            throw new BorrowingConflictException("Zakaznik, ktory ma vypozicane knihy nemoze byt odstraneny!");
        }
        repository.delete(customer);
    }

    public CustomerAndCountDto filterCustomers(CustomerFilter filter) {
        log.debug("filterCustomers: {}", filter);

        List<Customer> customers;
        if (!filter.getFirstName().isEmpty()) {
            filter.setFirstName("%" + filter.getFirstName().toLowerCase() + "%");
        }
        if (!filter.getLastName().isEmpty()) {
            filter.setLastName("%" + filter.getLastName().toLowerCase() + "%");
        }
        if (!filter.getEmail().isEmpty()) {
            filter.setEmail("%" + filter.getEmail() + "%");
        }
        if (!filter.getFirstName().isEmpty() && !filter.getLastName().isEmpty() && !filter.getEmail().isEmpty()) {
            customers = repository.list(
                    "Select e from Customer e where lower(e.firstName) like ?1 and lower(e.lastName) like ?2 and lower(e.email) like ?3",
                    Sort.by("id").ascending(),
                    filter.getFirstName(), filter.getLastName(), filter.getEmail()
            );
            return new CustomerAndCountDto(customers.size(), customers);
        }
        if (!filter.getFirstName().isEmpty() && !filter.getLastName().isEmpty()) {
            customers = repository.list(
                    "Select e from Customer e where lower(e.firstName) like ?1 and lower(e.lastName) like ?2",
                    Sort.by("id").ascending(),
                    filter.getFirstName(), filter.getLastName()
            );
            return new CustomerAndCountDto(customers.size(), customers);
        }
        if (!filter.getFirstName().isEmpty() && !filter.getEmail().isEmpty()) {
            customers = repository.list(
                    "Select e from Customer e where lower(e.firstName) like ?1 and lower(e.email) like ?2",
                    Sort.by("id").ascending(),
                    filter.getFirstName(), filter.getEmail()
            );
            return new CustomerAndCountDto(customers.size(), customers);
        }
        if (!filter.getLastName().isEmpty() && !filter.getEmail().isEmpty()) {
            customers = repository.list(
                    "Select e from Customer e where lower(e.lastName) like ?1 and lower(e.email) like ?2",
                    Sort.by("id").ascending(),
                    filter.getLastName(), filter.getEmail()
            );
            return new CustomerAndCountDto(customers.size(), customers);
        }
        if (!filter.getFirstName().isEmpty()) {
            customers = repository.list("Select e from Customer e where lower(e.firstName) like ?1",
                    Sort.by("id").ascending(),
                    filter.getFirstName()
            );
            return new CustomerAndCountDto(customers.size(), customers);
        }
        if (!filter.getLastName().isEmpty()) {
            customers = repository.list("Select e from Customer e where lower(e.lastName) like ?1",
                    Sort.by("id").ascending(),
                    filter.getLastName()
            );
            return new CustomerAndCountDto(customers.size(), customers);
        }
        if(!filter.getEmail().isEmpty()){
            customers = repository.list("Select e from Customer e where lower(e.email) like ?1",
                    Sort.by("id").ascending(),
                    filter.getEmail()
            );
            return new CustomerAndCountDto(customers.size(), customers);
        }

        customers = repository.listAll(Sort.by("id").ascending());
        return new CustomerAndCountDto(customers.size(), customers);
    }
}

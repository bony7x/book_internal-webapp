package com.example.customer.domain.service;

import com.example.borrow.exception.BorrowingConflictException;
import com.example.customer.domain.entity.Customer;
import com.example.customer.domain.repository.CustomerRepository;
import com.example.customer.exception.CustomerNotFoundException;
import com.example.request.ExtendedRequest;
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

    public List<Customer> getAll(ExtendedRequest request) {
        log.debug("getAll");

        if(request.getSortable().isAscending()){
            return repository.listAll(Sort.by(request.getSortable().getColumn()).ascending());
        } else {
            return repository.listAll(Sort.by(request.getSortable().getColumn()).descending());
        }
    }

    public List<Customer> getAll(){
        log.debug("getAll");

        return repository.listAll();
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
}

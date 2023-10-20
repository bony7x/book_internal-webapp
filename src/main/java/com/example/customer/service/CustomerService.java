package com.example.customer.service;

import com.example.customer.controller.dto.CustomerAndCountDto;
import com.example.customer.domain.entity.Customer;
import com.example.customer.domain.repository.CustomerRepository;
import com.example.customer.filter.CustomerFilter;
import com.example.utils.requests.ExtendedRequest;
import com.example.utils.calculateIndex.CalculateIndex;
import com.example.utils.calculateIndex.Index;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class CustomerService {

    @Inject
    CustomerRepository repository;

    public CustomerAndCountDto getAll(ExtendedRequest request) {
        log.debug("getAll");

        List<Customer> sublist;
        CustomerAndCountDto filteredCustomers;
        filteredCustomers = filterCustomers(request);
        if (filteredCustomers == null) {
            CalculateIndex calculateIndex = new CalculateIndex();
            List<Customer> customers = request.getSortable().isAscending() ? repository.listAll(
                    Sort.by(request.getSortable().getColumn()).ascending())
                    : repository.listAll(Sort.by(request.getSortable().getColumn()).descending());
            Index indexes = calculateIndex.calculateIndex(request, customers.size());
            sublist = customers.subList(indexes.getFromIndex(), indexes.getToIndex());
            return new CustomerAndCountDto(customers.size(), sublist);
        }
        CalculateIndex calculateIndex = new CalculateIndex();
        Index indexes = calculateIndex.calculateIndex(request, filteredCustomers.getCustomers().size());
        sublist = filteredCustomers.getCustomers().subList(indexes.getFromIndex(), indexes.getToIndex());
        return new CustomerAndCountDto(filteredCustomers.getCustomers().size(), sublist);
    }

    public CustomerAndCountDto filterCustomers(ExtendedRequest request) {
        log.debug("filterCustomers: {}", request);

        ObjectMapper objectMapper = new ObjectMapper();
        if (request.getFilter().isEmpty()) {
            CustomerFilter filter = objectMapper.convertValue(request.getFilter(), CustomerFilter.class);
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
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getFirstName(), filter.getLastName(), filter.getEmail()
                );
                return new CustomerAndCountDto(customers.size(), customers);
            }
            if (!filter.getFirstName().isEmpty() && !filter.getLastName().isEmpty()) {
                customers = repository.list(
                        "Select e from Customer e where lower(e.firstName) like ?1 and lower(e.lastName) like ?2",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getFirstName(), filter.getLastName()
                );
                return new CustomerAndCountDto(customers.size(), customers);
            }
            if (!filter.getFirstName().isEmpty() && !filter.getEmail().isEmpty()) {
                customers = repository.list(
                        "Select e from Customer e where lower(e.firstName) like ?1 and lower(e.email) like ?2",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getFirstName(), filter.getEmail()
                );
                return new CustomerAndCountDto(customers.size(), customers);
            }
            if (!filter.getLastName().isEmpty() && !filter.getEmail().isEmpty()) {
                customers = repository.list(
                        "Select e from Customer e where lower(e.lastName) like ?1 and lower(e.email) like ?2",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getLastName(), filter.getEmail()
                );
                return new CustomerAndCountDto(customers.size(), customers);
            }
            if (!filter.getFirstName().isEmpty()) {
                customers = repository.list("Select e from Customer e where lower(e.firstName) like ?1",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getFirstName()
                );
                return new CustomerAndCountDto(customers.size(), customers);
            }
            if (!filter.getLastName().isEmpty()) {
                customers = repository.list("Select e from Customer e where lower(e.lastName) like ?1",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getLastName()
                );
                return new CustomerAndCountDto(customers.size(), customers);
            }
            if (!filter.getEmail().isEmpty()) {
                customers = repository.list("Select e from Customer e where lower(e.email) like ?1",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getEmail()
                );
                return new CustomerAndCountDto(customers.size(), customers);
            }
        }
        return null;
    }
}

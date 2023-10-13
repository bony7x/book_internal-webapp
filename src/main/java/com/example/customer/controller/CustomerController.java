package com.example.customer.controller;

import com.example.book.controller.dto.BookDto;
import com.example.book.controller.dto.BookResponseDto;
import com.example.book.controller.dto.BooksAndCountDto;
import com.example.book.domain.entity.BookFilter;
import com.example.borrowing.exception.BorrowingConflictException;
import com.example.customer.controller.dto.CreateCustomerDto;
import com.example.customer.controller.dto.CustomerAndCountDto;
import com.example.customer.controller.dto.CustomerDto;
import com.example.customer.controller.dto.CustomerResponseDto;
import com.example.customer.controller.mapper.CustomerMapper;
import com.example.customer.domain.entity.Customer;
import com.example.customer.domain.entity.CustomerFilter;
import com.example.customer.domain.service.CustomerPersistenceService;
import com.example.customer.exception.CustomerNotFoundException;
import com.example.request.ExtendedRequest;
import com.example.request.Pageable;
import com.example.request.Sortable;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Path("/api")
@Slf4j
public class CustomerController {

    @Inject
    CustomerPersistenceService persistenceService;

    @Inject
    CustomerMapper mapper;

    @POST
    @Path("/customers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCustomer(@Valid CreateCustomerDto customerDto) {
        log.debug("createCustomer: {}", customerDto);

        Customer customer = persistenceService.persist(mapper.map(customerDto));
        CustomerDto dto = mapper.map(customer);
        return Response.status(201).entity(dto).build();
    }

    @POST
    @Path("/customers/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllCustomers(ExtendedRequest request) {
        log.debug("getAllCustomers: {}", request);

        CustomerAndCountDto customers = persistenceService.getAll(request);
        List<CustomerDto> dto = mapper.map(customers.getCustomers());
        CustomerResponseDto response = mapper.mapToResponse(dto, request, customers.getTotalCount());
        return Response.status(200).entity(response).build();
    }

    @POST
    @Path("/customers/filter")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response filterCustomers(ExtendedRequest request) {
        log.debug("filterCustomers: {}", request);

        CustomerAndCountDto customerAndCountDto = persistenceService.filterCustomers(request);
        List<CustomerDto> dtos = mapper.map(customerAndCountDto.getCustomers());
        ExtendedRequest er = new ExtendedRequest();
        er.setSortable(new Sortable("id",true));
        er.setPageable(new Pageable(1, customerAndCountDto.getTotalCount()));
        CustomerResponseDto responseDto = mapper.mapToResponse(dtos,er, customerAndCountDto.getTotalCount());
        return Response.status(200).entity(responseDto).build();
    }

    @GET
    @Path("/customers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomers(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName) {
        log.debug("getCustomers: {} {}", firstName, lastName);

        if (firstName != null && lastName != null && !firstName.isEmpty() && !lastName.isEmpty()) {
            List<Customer> customers = persistenceService.getAllByFirstNameAndLastName(firstName, lastName);
            List<CustomerDto> dto = mapper.map(customers);
            return Response.status(200).entity(dto).build();
        }
        if (firstName != null && !firstName.isEmpty()) {
            List<Customer> customers = persistenceService.getAllByFirstName(firstName);
            List<CustomerDto> dto = mapper.map(customers);
            return Response.status(200).entity(dto).build();
        }
        if (lastName != null && !lastName.isEmpty()) {
            List<Customer> customers = persistenceService.getAllByLastName(lastName);
            List<CustomerDto> dto = mapper.map(customers);
            return Response.status(200).entity(dto).build();
        }
        List<Customer> customers = persistenceService.getAll();
        List<CustomerDto> dto = mapper.map(customers);
        return Response.status(200).entity(dto).build();
    }

    @GET
    @Path("/customers/{customerId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomer(@PathParam("customerId") Integer id) {
        log.debug("getCustomer: {}", id);

        try {
            Customer customer = persistenceService.getCustomerById(id);
            CustomerDto dto = mapper.map(customer);
            List<CustomerDto> dtos = new ArrayList<>(List.of(dto));
            return Response.status(200).entity(dtos).build();
        } catch (Exception e) {
            return null;
        }
    }

    @PUT
    @Path("/customers/{customerId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCustomer(@PathParam("customerId") Integer id, @Valid CreateCustomerDto update) {
        log.debug("updateCustomer: {} {}", id, update);

        try {
            Customer customer = mapper.map(update);
            Customer updated = persistenceService.updateCustomer(id, customer);
            CustomerDto dto = mapper.map(updated);
            return Response.status(200).entity(dto).build();
        } catch (Exception e) {
            if (e.getClass().equals(CustomerNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/customers/{customerId}")
    public Response deleteCustomer(@PathParam("customerId") Integer id) {
        log.debug("deleteCustomer: {}", id);

        try {
            persistenceService.deleteCustomer(id);
            return Response.status(200).build();
        } catch (Exception e) {
            if (e.getClass().equals(CustomerNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            if (e.getClass().equals(BorrowingConflictException.class)) {
                return Response.status(409).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }
}

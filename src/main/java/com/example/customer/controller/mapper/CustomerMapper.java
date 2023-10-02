package com.example.customer.controller.mapper;

import com.example.borrowing.domain.entity.Borrowing;
import com.example.bookCategory.domain.entity.BookCategory;
import com.example.customer.controller.dto.CreateCustomerDto;
import com.example.customer.controller.dto.CustomerDto;
import com.example.customer.controller.dto.CustomerResponseDto;
import com.example.customer.controller.dto.CustomersDto;
import com.example.customer.domain.entity.Customer;
import com.example.request.ExtendedRequest;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public abstract class CustomerMapper {

    public CustomerResponseDto mapToResponse(List<CustomerDto> customers, ExtendedRequest extendedRequest, Integer size){
        CustomerResponseDto customerResponse = new CustomerResponseDto();
        customerResponse.setCustomers(customers);
        customerResponse.setPageSize(extendedRequest.getPageable().getPageSize());
        customerResponse.setPageNumber(extendedRequest.getPageable().getPageNumber());
        customerResponse.setTotalCount(size);
        return customerResponse;
    }

    public abstract Customer map(CreateCustomerDto dto);

    public abstract CustomerDto map(Customer customer);

    public List<Integer> mapBorrowingToId(List<Borrowing> value){
        List<Integer> list = new ArrayList<>();
        for( Borrowing b : value){
            list.add(b.getId());
        }
        return list;
    }

    public List<Integer> mapCategoryToId(List<BookCategory> value){
        List<Integer> list = new ArrayList<>();
        for (BookCategory b : value){
            list.add(b.getId());
        }
        return list;
    }

    public abstract List<CustomerDto> map(List<Customer> list);

    public CustomersDto mapToCustomers(List<CustomerDto> list){
        CustomersDto dto = new CustomersDto();
        dto.setCustomers(list);
        return dto;
    }
}

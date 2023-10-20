package com.example.customer.controller.mapper;

import com.example.customer.controller.dto.CreateCustomerDto;
import com.example.customer.controller.dto.CustomerDto;
import com.example.customer.controller.dto.CustomerResponseDto;
import com.example.customer.domain.entity.Customer;
import com.example.utils.requests.ExtendedRequest;
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

    public abstract List<CustomerDto> map(List<Customer> list);
}

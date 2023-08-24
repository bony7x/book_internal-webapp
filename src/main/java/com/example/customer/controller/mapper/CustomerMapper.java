package com.example.customer.controller.mapper;

import com.example.customer.controller.dto.CreateCustomerDto;
import com.example.customer.controller.dto.CustomerDto;
import com.example.customer.controller.dto.CustomersDto;
import com.example.customer.domain.entity.Customer;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public abstract class CustomerMapper {

    public abstract Customer map(CreateCustomerDto dto);

    public abstract CustomerDto map(Customer customer);

    public abstract List<CustomerDto> map(List<Customer> list);

    public CustomersDto mapToCustomers(List<CustomerDto> list){
        CustomersDto dto = new CustomersDto();
        dto.setCustomers(list);
        return dto;
    }
}

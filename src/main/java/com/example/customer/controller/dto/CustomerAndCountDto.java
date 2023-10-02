package com.example.customer.controller.dto;

import com.example.customer.domain.entity.Customer;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerAndCountDto {

    private Integer totalCount;

    private List<Customer> customers;
}

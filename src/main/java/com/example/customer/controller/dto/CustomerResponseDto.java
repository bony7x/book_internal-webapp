package com.example.customer.controller.dto;

import com.example.utils.responses.ExtendedResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseDto extends ExtendedResponse {

    List<CustomerDto> customers;
}

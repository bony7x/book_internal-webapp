package com.example.auth.controller.dto;

import com.example.customer.controller.dto.SendCustomerDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Integer id;

    private String name;

    private String password;

    private String email;

    private String role;

    private UserCustomerDto customer;
}

package com.example.auth.controller.dto;

import com.example.auth.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserAsCustomerDto {

    private String firstName;

    private String lastName;

    private String address;

    private User user;
}

package com.example.customer.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerDto {

    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String email;
}

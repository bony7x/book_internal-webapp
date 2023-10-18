package com.example.customer.controller.dto;

import io.quarkus.arc.All;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@All
public class CustomerOnlyDto {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String address;
}

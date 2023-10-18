package com.example.book.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCustomerOnlyDto {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private String address;
}

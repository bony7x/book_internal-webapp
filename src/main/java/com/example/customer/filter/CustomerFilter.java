package com.example.customer.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerFilter {

    private String firstName;
    private String lastName;
    private String email;
}

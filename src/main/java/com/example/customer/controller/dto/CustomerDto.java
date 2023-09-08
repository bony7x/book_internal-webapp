package com.example.customer.controller.dto;

import com.example.book.controller.dto.SendBorrowingsDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDto {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private List<SendBorrowingsDto> borrowings;
}

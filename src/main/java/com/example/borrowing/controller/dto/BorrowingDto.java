package com.example.borrowing.controller.dto;

import com.example.book.controller.dto.SendBookDto;
import com.example.customer.controller.dto.SendCustomerDto;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingDto {

    private Integer id;

    private SendBookDto book;

    private SendCustomerDto customer;

    private LocalDate dateOfBorrowing;
}

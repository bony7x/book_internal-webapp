package com.example.borrow.controller.dto;

import com.example.book.controller.dto.BookDto;
import com.example.customer.controller.dto.CustomerDto;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingDto {

    private Integer id;

    private Integer book;

    private Integer customer;

    private LocalDate dateOfBorrowing;
}

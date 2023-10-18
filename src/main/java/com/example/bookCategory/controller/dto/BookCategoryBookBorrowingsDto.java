package com.example.bookCategory.controller.dto;

import com.example.book.controller.dto.BookCustomerOnlyDto;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCategoryBookBorrowingsDto {

    private Integer id;

    private LocalDate dateOfBorrowing;

    private BookCustomerOnlyDto customer;

}

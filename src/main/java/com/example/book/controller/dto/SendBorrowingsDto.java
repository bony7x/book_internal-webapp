package com.example.book.controller.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendBorrowingsDto {

    private Integer id;

    private LocalDate dateOfBorrowing;

    private SendBookDto book;

}

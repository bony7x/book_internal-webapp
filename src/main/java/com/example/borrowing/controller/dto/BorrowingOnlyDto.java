package com.example.borrowing.controller.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowingOnlyDto {

    private Integer id;

    private LocalDate dateOfBorrowing;
}

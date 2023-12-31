package com.example.borrowing.filter;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowingFilter {

    private String name;
    private String email;
    private LocalDate date;
}

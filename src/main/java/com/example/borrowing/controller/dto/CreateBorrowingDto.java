package com.example.borrowing.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBorrowingDto {

    @NotNull
    private Integer bookId;

    @NotNull
    private Integer customerId;
}

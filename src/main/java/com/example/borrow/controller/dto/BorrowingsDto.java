package com.example.borrow.controller.dto;

import jakarta.persistence.Access;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowingsDto {

    private List<BorrowingDto> borrowings;
}

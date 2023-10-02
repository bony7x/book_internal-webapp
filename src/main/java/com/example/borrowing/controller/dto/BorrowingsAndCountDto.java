package com.example.borrowing.controller.dto;

import com.example.borrowing.domain.entity.Borrowing;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BorrowingsAndCountDto {

    private Integer totalCount;

    private List<Borrowing> borrowings;
}

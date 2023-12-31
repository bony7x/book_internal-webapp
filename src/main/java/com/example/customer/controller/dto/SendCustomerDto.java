package com.example.customer.controller.dto;

import com.example.borrowing.controller.dto.BorrowingOnlyDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendCustomerDto {
    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private List<BorrowingOnlyDto> borrowings;
}

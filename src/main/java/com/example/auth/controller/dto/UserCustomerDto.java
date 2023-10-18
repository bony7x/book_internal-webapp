package com.example.auth.controller.dto;

import com.example.borrowing.controller.dto.BorrowingDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCustomerDto {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private List<BorrowingDto> borrowings;
}

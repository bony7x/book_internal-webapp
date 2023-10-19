package com.example.bookCategory.controller.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCategoryDto {

    private Integer id;

    private String name;

    private List<BookCategoryBookDto> books;

    private Integer bookCount;
}

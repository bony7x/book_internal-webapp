package com.example.category.controller.dto;

import com.example.book.domain.entity.Book;
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

    private List<SendBooksDto> books;
}

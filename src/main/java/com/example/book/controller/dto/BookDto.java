package com.example.book.controller.dto;

import com.example.book.domain.entity.BookStatus;
import com.example.category.controller.dto.BookCategoryDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDto {

    private Integer id;

    private String name;

    private String author;

    private List<SendCategoriesDto> categories;

    private List<SendBorrowingsDto> borrowings;

    private Integer count;

    private BookStatus status;

    private String isbn;
}

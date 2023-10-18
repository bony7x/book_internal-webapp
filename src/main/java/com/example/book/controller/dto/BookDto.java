package com.example.book.controller.dto;

import com.example.book.domain.entity.BookStatus;
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

    private List<BookCategoriesOnlyDto> categories;

    private List<BookBorrowingDto> borrowings;

    private Integer count;

    private BookStatus status;

    private String isbn;
}

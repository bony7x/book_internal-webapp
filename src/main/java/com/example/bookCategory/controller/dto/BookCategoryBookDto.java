package com.example.bookCategory.controller.dto;

import com.example.book.controller.dto.BookBorrowingDto;
import com.example.book.domain.entity.BookStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCategoryBookDto {

    private Integer id;

    private String name;

    private String author;

    private List<BookCategoryBookBorrowingsDto> borrowings;

    private Integer count;

    private BookStatus status;

    private String isbn;
}

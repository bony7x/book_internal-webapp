package com.example.book.controller.dto;

import com.example.book.domain.entity.Book;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BooksAndCountDto {

    private Integer totalCount;

    private List<Book> books;
}

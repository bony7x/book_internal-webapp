package com.example.book.controller.dto;

import com.example.book.domain.entity.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookOnlyDto {

    private Integer id;

    private String name;

    private String author;

    private Integer count;

    private BookStatus status;

    private String isbn;
}

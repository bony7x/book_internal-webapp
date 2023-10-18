package com.example.bookCategory.controller.dto;

import com.example.book.domain.entity.BookStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryBookOnlyDto {

    private Integer id;

    private String name;

    private Integer count;

    private BookStatus status;

    private String isbn;

    private String author;
}

package com.example.bookCategory.controller.dto;

import com.example.bookCategory.domain.entity.BookCategory;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookCategoriesAndCountDto {

    private Integer totalCount;

    private List<BookCategory> bookCategories;
}

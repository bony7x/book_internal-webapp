package com.example.bookCategory.controller.dto;

import com.example.request.ExtendedResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCategoryResponseDto extends ExtendedResponse {

    List<BookCategoryDto> categories;
}

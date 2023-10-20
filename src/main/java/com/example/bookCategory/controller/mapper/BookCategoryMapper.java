package com.example.bookCategory.controller.mapper;

import com.example.book.domain.entity.Book;
import com.example.bookCategory.controller.dto.BookCategoryDto;
import com.example.bookCategory.controller.dto.BookCategoryResponseDto;
import com.example.bookCategory.controller.dto.CreateBookCategoryDto;
import com.example.bookCategory.domain.entity.BookCategory;
import com.example.utils.requests.ExtendedRequest;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public abstract class BookCategoryMapper {

    public BookCategoryResponseDto mapToResponse(List<BookCategoryDto> categories, ExtendedRequest extendedRequest, Integer size){
        BookCategoryResponseDto bookCategoryResponse = new BookCategoryResponseDto();
        bookCategoryResponse.setCategories(categories);
        bookCategoryResponse.setPageSize(extendedRequest.getPageable().getPageSize());
        bookCategoryResponse.setPageNumber(extendedRequest.getPageable().getPageNumber());
        bookCategoryResponse.setTotalCount(size);
        return bookCategoryResponse;
    }

    public abstract BookCategory map(CreateBookCategoryDto dto);

    public abstract BookCategoryDto map(BookCategory bookCategory);

    public  Integer map(Book value){
        Integer inte = value.getId();
        return  inte;
    }

    public abstract List<BookCategoryDto> map(List<BookCategory> bookCategories);

}

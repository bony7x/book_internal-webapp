package com.example.category.controller.mapper;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;
import com.example.book.controller.dto.BookDto;
import com.example.book.controller.dto.BookResponse;
import com.example.book.controller.dto.BooksDto;
import com.example.book.domain.entity.Book;
import com.example.category.controller.dto.BookCategoriesDto;
import com.example.category.controller.dto.BookCategoryDto;
import com.example.category.controller.dto.BookCategoryResponse;
import com.example.category.controller.dto.CreateBookCategoryDto;
import com.example.category.controller.dto.SendBooksDto;
import com.example.category.domain.entity.BookCategory;
import com.example.request.ExtendedRequest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public abstract class BookCategoryMapper {

    public BookCategoryResponse mapToResponse(List<BookCategoryDto> categories, ExtendedRequest extendedRequest, Integer size){
        BookCategoryResponse bookCategoryResponse = new BookCategoryResponse();
        bookCategoryResponse.setCategories(categories);
        bookCategoryResponse.setPageSize(extendedRequest.getPageable().getPageSize());
        bookCategoryResponse.setPageNumber(extendedRequest.getPageable().getPageNumber());
        bookCategoryResponse.setTotalCount(size);
        return bookCategoryResponse;
    }

    public abstract BookCategory map(CreateBookCategoryDto dto);

    public abstract BookCategoryDto map(BookCategory bookCategory);

    public abstract List<Integer> mapToInt(List<Book> value);

    public abstract List<SendBooksDto> mapToSend(List<BooksDto>list);

    public  Integer map(Book value){
        Integer inte = value.getId();
        return  inte;
    }

    public abstract List<BookCategoryDto> map(List<BookCategory> bookCategories);

    public BookCategoriesDto mapToCategories(List<BookCategoryDto> books){
        BookCategoriesDto dto = new BookCategoriesDto();
        dto.setCategories(books);
        return dto;
    }
}

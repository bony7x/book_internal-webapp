package com.example.category.controller.mapper;

import com.arjuna.ats.internal.jdbc.drivers.modifiers.list;
import com.example.book.controller.dto.BooksDto;
import com.example.book.domain.entity.Book;
import com.example.category.controller.dto.BookCategoriesDto;
import com.example.category.controller.dto.BookCategoryDto;
import com.example.category.controller.dto.CreateBookCategoryDto;
import com.example.category.controller.dto.SendBooksDto;
import com.example.category.domain.entity.BookCategory;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public abstract class BookCategoryMapper {

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

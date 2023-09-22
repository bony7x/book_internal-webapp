package com.example.book.controller.mapper;

import com.example.book.controller.dto.BookDto;
import com.example.book.controller.dto.BookResponse;
import com.example.book.controller.dto.BooksDto;
import com.example.book.controller.dto.CreateBookDto;
import com.example.book.controller.dto.SendBorrowingsDto;
import com.example.book.controller.dto.SendCategoriesDto;
import com.example.book.domain.entity.Book;
import com.example.borrow.domain.entity.Borrowing;
import com.example.category.domain.entity.BookCategory;
import com.example.request.ExtendedRequest;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public abstract class BookMapper {

    public BookResponse mapToResponse(List<BookDto> books, ExtendedRequest extendedRequest, Integer size){
        BookResponse bookResponse = new BookResponse();
        bookResponse.setBooks(books);
        bookResponse.setPageSize(extendedRequest.getPageable().getPageSize());
        bookResponse.setPageNumber(extendedRequest.getPageable().getPageNumber());
        bookResponse.setTotalCount(size);
        return bookResponse;
    }
    public abstract Book map(CreateBookDto dto);

    public abstract BookDto map(Book book);

    public List<Integer> mapToInt2(List<Borrowing> value){
        List<Integer> ints = new ArrayList<>();
        for (Borrowing b : value){
            ints.add(b.getId());
        }
        return ints;
    }

    public Integer map(Borrowing value){
        Integer inte = value.getId();
        return inte;
    }

    public List<Integer> mapToInt(List<BookCategory> value){
        List<Integer> ints = new ArrayList<>();
        for(BookCategory b : value){
            ints.add(b.getId());
        }
        return ints;
    }

    public abstract List<SendBorrowingsDto> mapToSend(List<BookDto> list);

    public abstract List<SendCategoriesDto> mapToSendCat(List<BookDto> list);

    public abstract List<BookDto> map(List<Book> books);

    public BooksDto mapToBooks(List<BookDto> list){
        BooksDto dto = new BooksDto();
        dto.setBooks(list);
        return dto;
    }
}

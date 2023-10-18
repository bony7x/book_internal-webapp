package com.example.book.controller.mapper;

import com.example.book.controller.dto.BookCategoriesOnlyDto;
import com.example.book.controller.dto.BookDto;
import com.example.book.controller.dto.BookResponseDto;
import com.example.book.controller.dto.BooksAndCountDto;
import com.example.book.controller.dto.CreateBookDto;
import com.example.book.controller.dto.BookBorrowingDto;
import com.example.book.domain.entity.Book;
import com.example.borrowing.domain.entity.Borrowing;
import com.example.bookCategory.domain.entity.BookCategory;
import com.example.request.ExtendedRequest;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public abstract class BookMapper {

    public BookResponseDto mapToResponse(List<BookDto> books, ExtendedRequest extendedRequest, Integer size){
        BookResponseDto bookResponse = new BookResponseDto();
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

    public abstract List<BookBorrowingDto> mapToSend(List<BookDto> list);

    public abstract List<BookCategoriesOnlyDto> mapToSendCat(List<BookDto> list);

    public abstract List<BookDto> map(List<Book> books);

    public BooksAndCountDto mapToBooks(List<Book> list){
        BooksAndCountDto dto = new BooksAndCountDto();
        dto.setBooks(list);
        return dto;
    }

}

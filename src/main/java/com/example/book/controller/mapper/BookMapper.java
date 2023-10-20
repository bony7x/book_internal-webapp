package com.example.book.controller.mapper;

import com.example.book.controller.dto.BookDto;
import com.example.book.controller.dto.BookResponseDto;
import com.example.book.controller.dto.CreateBookDto;
import com.example.book.domain.entity.Book;
import com.example.borrowing.domain.entity.Borrowing;
import com.example.utils.requests.ExtendedRequest;
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

    public Integer map(Borrowing value){
        Integer inte = value.getId();
        return inte;
    }

    public abstract List<BookDto> map(List<Book> books);


}

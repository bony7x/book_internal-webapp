package com.example.book.controller;

import com.example.book.controller.dto.BookDto;
import com.example.book.controller.dto.BookResponseDto;
import com.example.book.controller.dto.BooksAndCountDto;
import com.example.book.controller.dto.CreateBookDto;
import com.example.book.controller.mapper.BookMapper;
import com.example.book.domain.entity.Book;
import com.example.book.domain.service.BookPersistenceService;
import com.example.book.exception.BookConflictException;
import com.example.book.exception.BookNotFoundException;
import com.example.book.service.BookService;
import com.example.bookCategory.exception.BookCategoryNotFoundException;
import com.example.borrowing.exception.BorrowingConflictException;
import com.example.utils.requests.ExtendedRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Path("/api")
@Slf4j
public class BookController {

    @Inject
    BookPersistenceService persistenceService;

    @Inject
    BookService service;

    @Inject
    BookMapper mapper;

    @POST
    @Path(("/books"))
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBook(@Valid CreateBookDto createBookDto) {
        log.debug("createBook: {}", createBookDto);
        try {
            Book book = persistenceService.persist(mapper.map(createBookDto));
            BookDto dto = mapper.map(book);
            return Response.status(201).entity(dto).build();
        } catch (Exception e) {
            if (e.getClass().equals(BookConflictException.class)) {
                return Response.status(409).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/books/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllBooks(ExtendedRequest request) {
        log.debug("getAllBooks: {}", request);

        BooksAndCountDto booksAndCountDto = service.getAllBooks(request);
        List<BookDto> dtos = mapper.map(booksAndCountDto.getBooks());
        BookResponseDto response = mapper.mapToResponse(dtos, request, booksAndCountDto.getTotalCount());
        return Response.status(200).entity(response).build();
    }


    @GET
    @Path("/books")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooks(@QueryParam("name") String name, @QueryParam("categoryId") Integer categoryId) {
        log.debug("getBooks: {} {}", name, categoryId);

        if (name != null && !name.isEmpty()) {
            List<Book> books = persistenceService.getAllByName(name);
            List<BookDto> dtos = mapper.map(books);
            return Response.status(200).entity(dtos).build();
        }
        if (categoryId != null) {
            List<Book> books = persistenceService.getAllByCategoryId(categoryId);
            List<BookDto> dtos = mapper.map(books);
            return Response.status(200).entity(dtos).build();
        }
        List<Book> books = persistenceService.getAllBooks();
        List<BookDto> dtos = mapper.map(books);
        return Response.status(200).entity(dtos).build();
    }

    @GET
    @Path("/books/{bookId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBook(@PathParam("bookId") Integer id) {
        log.debug("getBook: {}", id);

        try {
            Book book = persistenceService.getById(id);
            BookDto dto = mapper.map(book);
            List<BookDto> dtos = new ArrayList<>(List.of(dto));
            return Response.status(200).entity(dtos).build();
        } catch (Exception e) {
            return null;
        }
    }

    @PUT
    @Path("/books/{bookId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBook(@PathParam("bookId") Integer id, @Valid CreateBookDto createBookDto) {
        log.debug("updateBook: {}", id);

        try {
            Book update = mapper.map(createBookDto);
            Book book = persistenceService.updateBook(id, update);
            BookDto dto = mapper.map(book);
            return Response.status(200).entity(dto).build();
        } catch (Exception e) {
            if (e.getClass().equals(BookNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/books/{bookId}")
    public Response deleteBook(@PathParam("bookId") Integer id) {
        log.debug("deleteBook: {}", id);

        try {
            persistenceService.deleteBook(id);
            return Response.status(200).build();
        } catch (Exception e) {
            if (e.getClass().equals(BookNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            if (e.getClass().equals(BorrowingConflictException.class)) {
                return Response.status(409).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }


    @PUT
    @Path("/books/{bookId}/bookCategory")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBookCategories(@PathParam("bookId") Integer bookId, Integer[] categoryIds) {
        log.debug("updateBookCategories: {} {}", bookId, categoryIds);

        try {
            Book book = service.updateCategories(bookId, categoryIds);
            BookDto dto = mapper.map(book);
            return Response.status(200).entity(dto).build();
        } catch (Exception e) {
            if (e.getClass().equals(BookNotFoundException.class) ||
                    e.getMessage().equals(BookCategoryNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }
}


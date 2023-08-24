package com.example.book.controller;

import com.example.book.controller.dto.BookDto;
import com.example.book.controller.dto.BooksDto;
import com.example.book.controller.dto.CreateBookDto;
import com.example.book.controller.mapper.BookMapper;
import com.example.book.domain.entity.Book;
import com.example.book.domain.service.BookPersistenceService;
import com.example.book.exception.BookCategoryConflictException;
import com.example.book.exception.BookNotFoundException;
import com.example.book.service.BookService;
import com.example.borrow.exception.BorrowingConflictException;
import com.example.category.exception.BookCategoryNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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

        Book book = persistenceService.persist(mapper.map(createBookDto));
        BookDto dto = mapper.map(book);
        return Response.status(201).entity(dto).build();
    }

    @GET
    @Path("/books")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooks(@QueryParam("name") String name, @QueryParam("categoryId") Integer categoryId) {
        log.debug("getBooks: {} {}", name, categoryId);

        if (name != null && categoryId != null) {
            List<Book> books = persistenceService.getAllByNameAndCategoryId(name, categoryId);
            List<BookDto> dtos = mapper.map(books);
            return Response.status(200).entity(dtos).build();
        }
        if (name != null) {
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
            return Response.status(200).entity(dto).build();
        } catch (Exception e) {
            return Response.status(404).entity(e.getMessage()).build();
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
    @Path("/books/{bookId}/bookCategory/{bookCategoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBookCategory(@PathParam("bookId") Integer bookId, @PathParam("bookCategoryId") Integer
            bookCategoryId) {
        log.debug("addBookCategory: {} {}", bookId, bookCategoryId);

        try {
            Book book = service.addCategory(bookId, bookCategoryId);
            BookDto dto = mapper.map(book);
            return Response.status(200).entity(dto).build();
        } catch (Exception e) {
            if (e.getClass().equals(BookNotFoundException.class) ||
                    e.getClass().equals(BookCategoryNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            if (e.getClass().equals(BookCategoryConflictException.class)) {
                return Response.status(409).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/books/{bookId}/bookCategory/{bookCategoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeBookCategory(@PathParam("bookId") Integer bookId, @PathParam("bookCategoryId") Integer
            bookCategoryId) {
        log.debug("removeBookCategory: {} {}", bookId, bookCategoryId);

        try {
            Book book = service.deleteCategory(bookId, bookCategoryId);
            BookDto dto = mapper.map(book);
            return Response.status(200).entity(dto).build();
        } catch (Exception e) {
            if (e.getClass().equals(BookNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            if (e.getClass().equals(BookCategoryConflictException.class)) {
                return Response.status(409).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }
}


package com.example.category.controller;

import com.example.book.controller.dto.BookResponse;
import com.example.book.domain.entity.Book;
import com.example.category.controller.dto.BookCategoriesDto;
import com.example.category.controller.dto.BookCategoryDto;
import com.example.category.controller.dto.BookCategoryResponse;
import com.example.category.controller.dto.CreateBookCategoryDto;
import com.example.category.controller.mapper.BookCategoryMapper;
import com.example.category.domain.entity.BookCategory;
import com.example.category.domain.service.BookCategoryPersistenceService;
import com.example.category.exception.BookCategoryNotFoundException;
import com.example.request.ExtendedRequest;
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
public class BookCategoryController {

    @Inject
    BookCategoryPersistenceService persistenceService;

    @Inject
    BookCategoryMapper mapper;

    @POST
    @Path("/bookCategories")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBookCategory(@Valid CreateBookCategoryDto dto) {
        log.debug("createBookCategory: {}", dto);

        BookCategory bookCategory = persistenceService.persist(mapper.map(dto));
        BookCategoryDto responseDto = mapper.map(bookCategory);
        return Response.status(201).entity(responseDto).build();
    }

    @POST
    @Path("/bookCategories/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllBookCategories(ExtendedRequest request){
        log.debug("getAllBookCategories: {}", request);

        int fromIndex = 0;
        int toIndex;
        List<BookCategory> bookCategories = persistenceService.getAll(request);
        if (request.getPageable().getPageNumber() != 1) {
            fromIndex = (request.getPageable().getPageNumber() - 1) * request.getPageable().getPageSize();
        }
        if (fromIndex + request.getPageable().getPageSize() > bookCategories.size()) {
            toIndex = bookCategories.size();
        } else {
            toIndex = fromIndex + request.getPageable().getPageSize();
        }
        List<BookCategory> sublist = bookCategories.subList(fromIndex, toIndex);
        List<BookCategoryDto> dtos = mapper.map(sublist);
        BookCategoryResponse response = mapper.mapToResponse(dtos, request, bookCategories.size());
        return Response.status(200).entity(response).build();
    }

    @GET
    @Path("/bookCategories")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookCategories(@QueryParam("name") String name) {
        log.debug("getBookCategories: {}", name);

        if(name!= null){
            List<BookCategory> books = persistenceService.getAllByName(name);
            List<BookCategoryDto> dtos = mapper.map(books);
            return Response.status(200).entity(dtos).build();
        }
        List<BookCategory> books = persistenceService.getAll();
        List<BookCategoryDto> dtos = mapper.map(books);
        return Response.status(200).entity(dtos).build();
    }

    @GET
    @Path("/bookCategories/{bookCategoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookCategory(@PathParam("bookCategoryId") Integer id) {
        log.debug("getBookCategory: {}", id);

        try {
            BookCategory bookCategory = persistenceService.getBookCategory(id);
            BookCategoryDto dto = mapper.map(bookCategory);
            return Response.status(200).entity(dto).build();
        } catch (Exception e) {
            if (e.getClass().equals(BookCategoryNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/bookCategories/{bookCategoryId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBookCategory(@PathParam("bookCategoryId") Integer id, @Valid CreateBookCategoryDto update) {
        log.debug("updateBookCategory: {}", update);

        BookCategory up = mapper.map(update);
        try {
            BookCategory bookCategory = persistenceService.updateBookCategory(id, up);
            BookCategoryDto dto = mapper.map(bookCategory);
            return Response.status(200).entity(dto).build();
        } catch (Exception e) {
            if (e.getClass().equals(BookCategoryNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/bookCategories/{bookCategoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBookCategory(@PathParam("bookCategoryId") Integer id) {
        log.debug("deleteBookCategory: {}", id);

        try {
            persistenceService.deleteBookCategory(id);
            return Response.status(200).build();
        } catch (Exception e) {
            if (e.getClass().equals(BookCategoryNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }
}

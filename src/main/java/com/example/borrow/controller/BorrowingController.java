package com.example.borrow.controller;

import com.example.book.exception.BookNotFoundException;
import com.example.borrow.controller.dto.BorrowingDto;
import com.example.borrow.controller.dto.BorrowingsDto;
import com.example.borrow.controller.dto.CreateBorrowingDto;
import com.example.borrow.controller.mapper.BorrowingMapper;
import com.example.borrow.domain.entity.Borrowing;
import com.example.borrow.domain.service.BorrowingPersistenceService;
import com.example.borrow.exception.BorrowingConflictException;
import com.example.borrow.exception.BorrowingNotFoundException;
import com.example.customer.exception.CustomerNotFoundException;
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
@Slf4j
@Path("/api")
public class BorrowingController {

    @Inject
    BorrowingPersistenceService persistenceService;

    @Inject
    BorrowingMapper mapper;

    @POST
    @Path("/borrowings")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBorrowing(@Valid CreateBorrowingDto dto) {
        log.debug("createBorrowing: {}", dto);

        try {
            Borrowing borrowing = persistenceService.createBorrowing(dto.getBookId(), dto.getCustomerId());
            BorrowingDto borrowingDto = mapper.mapToDto(borrowing);
            return Response.status(201).entity(borrowingDto).build();
        } catch (Exception e) {
            if (e.getClass().equals(BookNotFoundException.class) || e.getClass()
                    .equals(CustomerNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            if (e.getClass().equals(BorrowingConflictException.class)) {
                return Response.status(409).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/borrowings")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBorrowings(@QueryParam("bookId") Integer bookId, @QueryParam("customerId") Integer customerId) {
        log.debug("getBorrowings: {} {}", bookId, customerId);

        if (bookId != null && customerId != null) {
            List<Borrowing> borrowings = persistenceService.getBorrowingsByBookIdAndCustomerId(bookId, customerId);
            List<BorrowingDto> dto = mapper.mapToBDto(borrowings);
            return Response.status(200).entity(dto).build();
        }
        if (bookId != null) {
            List<Borrowing> borrowings = persistenceService.getBorrowingsByBookId(bookId);
            List<BorrowingDto> dto = mapper.mapToBDto(borrowings);
            return Response.status(200).entity(dto).build();
        }
        if (customerId != null) {
            List<Borrowing> borrowings = persistenceService.getBorrowingsByCustomerId(customerId);
            List<BorrowingDto> dto = mapper.mapToBDto(borrowings);
            return Response.status(200).entity(dto).build();
        }
        List<Borrowing> borrowings = persistenceService.getBorrowings();
        List<BorrowingDto> dto = mapper.mapToBDto(borrowings);
        return Response.status(200).entity(dto).build();
    }

    @GET
    @Path("/borrowings/{borrowingId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBorrowing(@PathParam("borrowingId") Integer id) {
        log.debug("getBorrowing: {}", id);

        try {
            Borrowing borrowing = persistenceService.getBorrowingById(id);
            BorrowingDto dto = mapper.mapToDto(borrowing);
            return Response.status(200).entity(dto).build();
        } catch (Exception e) {
            if (e.getClass().equals(BorrowingNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/borrowings/{borrowingId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateBorrowing(@PathParam("borrowingId") Integer id,@Valid  CreateBorrowingDto updateDto) {
        log.debug("updateBorrowing: {} {}", id, updateDto);

        try {
            Borrowing borrowing = persistenceService.updateBorrowing(id, updateDto.getBookId(),
                    updateDto.getCustomerId());
            BorrowingDto dto = mapper.mapToDto(borrowing);
            return Response.status(200).entity(dto).build();
        } catch (Exception e) {
            if (e.getClass().equals(BorrowingNotFoundException.class) || e.getClass()
                    .equals(BookNotFoundException.class) || e.getClass().equals(
                    CustomerNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/borrowings/{borrowingId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBorrowing(@PathParam("borrowingId") Integer id) {
        log.debug("deleteBorrowing: {}", id);

        try {
            persistenceService.deleteBorrowing(id);
            return Response.status(200).build();
        } catch (Exception e) {
            if (e.getClass().equals(BorrowingNotFoundException.class)) {
                return Response.status(404).entity(e.getMessage()).build();
            }
            return Response.status(400).entity(e.getMessage()).build();
        }
    }
}

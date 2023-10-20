package com.example.borrowing.service;

import com.example.borrowing.controller.dto.BorrowingsAndCountDto;
import com.example.borrowing.domain.entity.Borrowing;
import com.example.borrowing.domain.repository.BorrowingRepository;
import com.example.borrowing.filter.BorrowingFilter;
import com.example.utils.requests.ExtendedRequest;
import com.example.utils.calculateIndex.CalculateIndex;
import com.example.utils.calculateIndex.Index;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class BorrowingService {

    @Inject
    BorrowingRepository repository;

    public BorrowingsAndCountDto getBorrowings(ExtendedRequest request) {
        log.debug("getBorrowings: {}", request);

        List<Borrowing> sublist;
        BorrowingsAndCountDto filteredBorrowings;
        filteredBorrowings = filterBorrowings(request);
        if (filteredBorrowings == null) {
            CalculateIndex calculateIndex = new CalculateIndex();
            List<Borrowing> borrowings = request.getSortable().isAscending() ? repository.listAll(
                    Sort.by(request.getSortable().getColumn()).ascending())
                    : repository.listAll(Sort.by(request.getSortable().getColumn()).descending());
            Index indexes = calculateIndex.calculateIndex(request, borrowings.size());
            sublist = borrowings.subList(indexes.getFromIndex(), indexes.getToIndex());
            return new BorrowingsAndCountDto(borrowings.size(), sublist);
        }
        CalculateIndex calculateIndex = new CalculateIndex();
        Index indexes = calculateIndex.calculateIndex(request, filteredBorrowings.getBorrowings().size());
        sublist = filteredBorrowings.getBorrowings().subList(indexes.getFromIndex(), indexes.getToIndex());
        return new BorrowingsAndCountDto(filteredBorrowings.getBorrowings().size(), sublist);
    }

    public BorrowingsAndCountDto filterBorrowings(ExtendedRequest request) {
        log.debug("filterBorrowings: {}", request);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        if ( request.getFilter() != null && !request.getFilter().isEmpty()) {
            BorrowingFilter filter = objectMapper.convertValue(request.getFilter(), BorrowingFilter.class);
            List<Borrowing> borrowings;
            if (!filter.getName().isEmpty()) {
                filter.setName("%" + filter.getName().toLowerCase() + "%");
            }
            if (!filter.getEmail().isEmpty()) {
                filter.setEmail("%" + filter.getEmail().toLowerCase() + "%");
            }
            if (!filter.getName().isEmpty() && !filter.getEmail().isEmpty() && filter.getDate() != null) {
                borrowings = repository.list(
                        "Select e from Borrowing e where lower(e.customer.email) like ?1 and lower(e.book.name) like ?2 and e.dateOfBorrowing = ?3",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getEmail(), filter.getName(), filter.getDate()
                );
                return new BorrowingsAndCountDto(borrowings.size(), borrowings);
            }
            if (!filter.getName().isEmpty() && !filter.getEmail().isEmpty()) {
                borrowings = repository.list(
                        "Select e from Borrowing e where lower(e.customer.email) like ?1 and lower(e.book.name) like ?2",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getEmail(), filter.getName()
                );
                return new BorrowingsAndCountDto(borrowings.size(), borrowings);
            }
            if (!filter.getEmail().isEmpty() && filter.getDate() != null) {
                borrowings = repository.list(
                        "Select e from Borrowing e where lower(e.customer.email) like ?1 and e.dateOfBorrowing = ?2",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getEmail(), filter.getDate()
                );
                return new BorrowingsAndCountDto(borrowings.size(), borrowings);
            }
            if (!filter.getName().isEmpty() && filter.getDate() != null) {
                borrowings = repository.list(
                        "Select e from Borrowing e where lower(e.book.name) like ?1 and e.dateOfBorrowing = ?2",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getName(), filter.getDate()
                );
                return new BorrowingsAndCountDto(borrowings.size(), borrowings);
            }
            if (!filter.getName().isEmpty()) {
                borrowings = repository.list("Select e from Borrowing e where lower(e.book.name) like ?1",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getName()
                );
                return new BorrowingsAndCountDto(borrowings.size(), borrowings);
            }
            if (!filter.getEmail().isEmpty()) {
                borrowings = repository.list("Select e from Borrowing e where lower(e.customer.email) like ?1",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getEmail()
                );
                return new BorrowingsAndCountDto(borrowings.size(), borrowings);
            }
            if (filter.getDate() != null) {
                borrowings = repository.list("Select e from Borrowing e where e.dateOfBorrowing = ?1",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getDate()
                );
                return new BorrowingsAndCountDto(borrowings.size(), borrowings);
            }
        }
        return null;
    }
}

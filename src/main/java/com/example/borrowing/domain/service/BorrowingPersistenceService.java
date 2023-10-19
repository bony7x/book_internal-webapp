package com.example.borrowing.domain.service;

import com.example.book.domain.entity.Book;
import com.example.book.domain.entity.BookStatus;
import com.example.book.domain.service.BookPersistenceService;
import com.example.book.exception.BookNotFoundException;
import com.example.borrowing.controller.dto.BorrowingsAndCountDto;
import com.example.borrowing.domain.entity.Borrowing;
import com.example.borrowing.domain.entity.BorrowingFilter;
import com.example.borrowing.domain.repository.BorrowingRepository;
import com.example.borrowing.exception.BorrowingConflictException;
import com.example.borrowing.exception.BorrowingNotFoundException;
import com.example.customer.domain.entity.Customer;
import com.example.customer.domain.service.CustomerPersistenceService;
import com.example.customer.exception.CustomerNotFoundException;
import com.example.request.ExtendedRequest;
import com.example.utils.CalculateIndex.CalculateIndex;
import com.example.utils.CalculateIndex.Index;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class BorrowingPersistenceService {

    @Inject
    BorrowingRepository repository;

    @Inject
    BookPersistenceService bookPersistenceService;

    @Inject
    CustomerPersistenceService customerPersistenceService;

    @Transactional
    public Borrowing createBorrowing(Integer bookId, Integer customerId)
            throws BookNotFoundException, CustomerNotFoundException, BorrowingConflictException {
        log.debug("createBorrowing: {} {}", bookId, customerId);

        Borrowing borrowing = new Borrowing();
        Book book = bookPersistenceService.getById(bookId);
        Customer customer = customerPersistenceService.getCustomerById(customerId);
        if (book.getCount() == 0) {
            throw new BorrowingConflictException("This book is not available!");
        }
        book.setCount(book.getCount() - 1);
        if (book.getCount() == 0) {
            book.setStatus(BookStatus.NOT_AVAILABLE);
        }
        book.setBorrowingCount(book.getBorrowingCount() + 1);
        borrowing.setBook(book);
        borrowing.setCustomer(customer);
        borrowing.setDateOfBorrowing(LocalDate.now());
        customer.setBorrowingCount(customer.getBorrowingCount() + 1);
        repository.persist(borrowing);
        return borrowing;
    }

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

    public List<Borrowing> getBorrowingsByBookId(Integer id) {
        log.debug("getBorrowingsByBookId: {}", id);

        return repository.list("Select e from Borrowing e where e.book.id = ?1", id);
    }

    public List<Borrowing> getBorrowingsByCustomerId(Integer id) {
        log.debug("getBorrowingsByCustomerId: {}", id);

        return repository.list("Select e from Borrowing e where e.customer.id = ?1", id);
    }


    public Borrowing getBorrowingById(Integer id) throws BorrowingNotFoundException {
        log.debug("getBorrowingByid: {}", id);

        Borrowing borrowing = repository.findById(Long.valueOf(id));
        if (borrowing != null) {
            return borrowing;
        }
        throw new BorrowingNotFoundException(String.format("Vypozicanie s ID = %s nebolo najdene", id));
    }

    public List<Borrowing> getBorrowingsById(Integer id) throws BorrowingNotFoundException {
        log.debug("getBorrowingById: {}", id);

        List<Borrowing> borrowing = repository.list("Select e from Borrowing e where e.id = ?1", id);
        if (borrowing != null) {
            return borrowing;
        }
        throw new BorrowingNotFoundException(String.format("Vypozicanie s ID = %s nebolo najdene", id));
    }

    @Transactional
    public Borrowing updateBorrowing(Integer id, Integer bookId, Integer customerId)
            throws BookNotFoundException, CustomerNotFoundException, BorrowingNotFoundException {
        log.debug("updateBorrowing: {} {} {}", id, bookId, customerId);

        Book book = bookPersistenceService.getById(bookId);
        Customer customer = customerPersistenceService.getCustomerById(customerId);
        Borrowing borrowing = getBorrowingById(id);
        borrowing.setCustomer(customer);
        borrowing.setBook(book);
        repository.getEntityManager().merge(borrowing);
        return borrowing;
    }

    @Transactional
    public void deleteBorrowing(Integer id) throws BorrowingNotFoundException {
        log.debug("deleteBorrowing: {}", id);

        Borrowing borrowing = getBorrowingById(id);
        borrowing.getBook().setCount(borrowing.getBook().getCount() + 1);
        if (borrowing.getBook().getCount() == 1) {
            borrowing.getBook().setStatus(BookStatus.AVAILABLE);
        }
        borrowing.getBook().setBorrowingCount(borrowing.getBook().getBorrowingCount() - 1);
        borrowing.getCustomer().setBorrowingCount(borrowing.getCustomer().getBorrowingCount() - 1);
        repository.delete(borrowing);
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

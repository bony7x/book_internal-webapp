package com.example.borrow.domain.service;

import com.example.book.domain.entity.Book;
import com.example.book.domain.service.BookPersistenceService;
import com.example.book.exception.BookNotFoundException;
import com.example.borrow.domain.entity.Borrowing;
import com.example.borrow.domain.repository.BorrowingRepository;
import com.example.borrow.exception.BorrowingConflictException;
import com.example.borrow.exception.BorrowingNotFoundException;
import com.example.customer.domain.entity.Customer;
import com.example.customer.domain.service.CustomerPersistenceService;
import com.example.customer.exception.CustomerNotFoundException;
import io.quarkus.panache.common.Sort;
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
        borrowing.setBook(book);
        borrowing.setCustomer(customer);
        borrowing.setDateOfBorrowing(LocalDate.now());
        repository.persist(borrowing);
        return borrowing;
    }

    public List<Borrowing> getBorrowings() {
        log.debug("getBorrowings");

        return repository.listAll(Sort.by("id").ascending());
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
        repository.delete(borrowing);
    }
}

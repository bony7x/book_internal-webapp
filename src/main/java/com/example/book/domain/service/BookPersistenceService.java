package com.example.book.domain.service;

import com.example.book.domain.entity.Book;
import com.example.book.domain.entity.BookStatus;
import com.example.book.domain.repository.BookRepository;
import com.example.book.exception.BookConflictException;
import com.example.book.exception.BookNotFoundException;
import com.example.bookCategory.domain.entity.BookCategory;
import com.example.borrowing.exception.BorrowingConflictException;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class BookPersistenceService {

    @Inject
    BookRepository repository;

    public BookPersistenceService(BookRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Book persist(Book book) throws BookConflictException {
        log.debug("persist: {}", book);

        String name = "%" + book.getName().toLowerCase() + "%";
        String author = "%" + book.getAuthor().toLowerCase() + "%";
        Optional<Book> existing = repository.list("Select e from Book e where lower(e.name) like ?1 and lower(e.author) like ?2",
                name, author).stream().findFirst();
        if(existing.isPresent()) {
            throw new BookConflictException("Book with the given name and author is already listed!");
        }
        book.setIsbn(rng());
        if (book.getCount() > 0) {
            book.setStatus(BookStatus.AVAILABLE);
        } else {
            book.setStatus(BookStatus.NOT_AVAILABLE);
        }
        book.setBorrowingCount(0);
        repository.persist(book);
        return book;
    }

    public List<Book> getAllBooks() {
        log.debug("getAllBooks");

        return repository.listAll(Sort.by("id").ascending());
    }

    public List<Book> getAllByName(String name) {
        log.debug("getAllByName: {}", name);
        name = "%" + name + "%";
        name = name.toLowerCase();

        return repository.list("select e from Book e where lower(e.name) like ?1", name);
    }

    public List<Book> getAllByCategoryId(Integer categoryId) {
        log.debug("getAllByName: {}", categoryId);

        return repository.list("Select e from Book e join fetch e.categories c where c.id = ?1", categoryId);
    }

    public Book getById(Integer id) throws BookNotFoundException {
        log.debug("getById: {}", id);

        if (id != null) {
            Book book = repository.findById(id.longValue());
            if (book != null) {
                return book;
            }
            throw new BookNotFoundException(String.format("Kniha s ID = %s nenajdena", id));
        }
        throw new BookNotFoundException("ID knihy nemoze byt null");
    }

    @Transactional
    public Book updateBook(Integer id, Book update) throws BookNotFoundException {
        log.debug("updateBook: {} {}", id, update);

        Book book = getById(id);
        book.setAuthor(update.getAuthor());
        book.setName(update.getName());
        book.setCount(update.getCount());
        repository.getEntityManager().merge(book);
        return book;
    }

    @Transactional
    public void deleteBook(Integer id) throws BookNotFoundException, BorrowingConflictException {
        log.debug("deleteBook: {}", id);

        Book book = getById(id);
        if (!book.getBorrowings().isEmpty()) {
            throw new BorrowingConflictException("Vypozicana kniha nemoze byt odstranena!");
        }
        for (BookCategory bookCategory : book.getCategories()) {
            bookCategory.setBookCount(bookCategory.getBookCount() - 1);
        }
        repository.delete(book);
    }

    private String rng() {
        StringBuilder sb = new StringBuilder();
        int num;
        for (int i = 0; i < 12; i++) {
            Random random = new Random();
            num = random.nextInt(1, 9);
            sb.append(num);
        }
        return sb.toString();
    }
}

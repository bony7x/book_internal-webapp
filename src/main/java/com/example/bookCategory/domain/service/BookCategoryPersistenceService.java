package com.example.bookCategory.domain.service;

import com.example.book.domain.entity.Book;
import com.example.book.domain.repository.BookRepository;
import com.example.book.domain.service.BookPersistenceService;
import com.example.book.exception.BookCategoryConflictException;
import com.example.bookCategory.domain.entity.BookCategory;
import com.example.bookCategory.domain.repository.BookCategoryRepository;
import com.example.bookCategory.exception.BookCategoryNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class BookCategoryPersistenceService {

    @Inject
    BookCategoryRepository repository;

    @Inject
    BookRepository bookRepository;

    @Inject
    BookPersistenceService bookPersistenceService;

    public BookCategoryPersistenceService(BookCategoryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public BookCategory persist(BookCategory bookCategory) throws BookCategoryConflictException {
        log.debug("persist: {}", bookCategory);

        List<BookCategory> exists = repository.list("Select e from BookCategory e where e.name = ?1",
                bookCategory.getName());
        if (!exists.isEmpty()) {
            throw new BookCategoryConflictException("Book category with that name already exists!");
        }
        bookCategory.setBookCount(0);
        repository.persist(bookCategory);
        return bookCategory;
    }

    public List<BookCategory> getAllByName(String name) {
        log.debug("getAllByName: {}", name);
        name = "%" + name.toLowerCase() + "%";

        return repository.list("Select e from BookCategory e where lower(e.name) like ?1", name);
    }

    public BookCategory getBookCategory(Integer id) throws BookCategoryNotFoundException {
        log.debug("getBookCategory: {}", id);

        if (id == null) {
            throw new BookCategoryNotFoundException("ID can't be null!");
        }
        BookCategory bookCategory = repository.findById(Long.valueOf(id));
        if (bookCategory == null) {
            throw new BookCategoryNotFoundException(String.format("Book category with id = %s not found", id));
        }
        return bookCategory;
    }


    @Transactional
    public BookCategory updateBookCategory(Integer id, BookCategory update) throws BookCategoryNotFoundException {
        log.debug("updateBookCategory: {} {}", id, update);

        BookCategory bookCategory = getBookCategory(id);
        bookCategory.setName(update.getName());
        repository.getEntityManager().merge(bookCategory);
        return bookCategory;
    }

    @Transactional
    public void deleteBookCategory(Integer id) throws BookCategoryNotFoundException {
        log.debug("deleteBookCategory: {}", id);

        BookCategory bookCategory = getBookCategory(id);
        List<Book> books = bookPersistenceService.getAllByCategoryId(id);
        for (Book book : books) {
            book.getCategories().remove(bookCategory);
            bookRepository.getEntityManager().merge(book);
        }
        repository.delete(bookCategory);
    }
}

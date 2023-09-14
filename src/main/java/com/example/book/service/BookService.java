package com.example.book.service;

import com.example.book.domain.entity.Book;
import com.example.book.domain.service.BookPersistenceService;
import com.example.book.exception.BookCategoryConflictException;
import com.example.book.exception.BookNotFoundException;
import com.example.category.domain.entity.BookCategory;
import com.example.category.domain.service.BookCategoryPersistenceService;
import com.example.category.exception.BookCategoryNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class BookService {

    @Inject
    BookPersistenceService service;

    @Inject
    BookCategoryPersistenceService bookCategoryPersistenceService;

    @Transactional
    public Book addCategory(Integer bookId, Integer[] categoryId)
            throws BookNotFoundException, BookCategoryNotFoundException, BookCategoryConflictException {
        log.debug("addCategory: {} {}", bookId, categoryId);

        Book book = service.getById(bookId);
        for (Integer integer : categoryId) {
            BookCategory category = bookCategoryPersistenceService.getBookCategory(integer);
            boolean addable = book.getCategories().stream().anyMatch(bookCategory -> bookCategory.equals(category));
            if (!addable) {
                book.getCategories().add(category);
            } else {
                throw new BookCategoryConflictException(
                        String.format("Kniha uz je zaradena do kategorie s nazvom %s", category.getName()));
            }
        }
        return service.updateBookCategories(bookId, book);
    }


    @Transactional
    public Book deleteCategory(Integer bookId, Integer[] categoryId)
            throws BookNotFoundException, BookCategoryConflictException {
        log.debug("deleteCategory: {} {}", bookId, categoryId);

        Book book = service.getById(bookId);
        for (Integer integer : categoryId) {
            Optional<BookCategory> bookCategory1 = book.getCategories()
                    .stream()
                    .filter(bookCategory -> bookCategory.getId().equals(integer)).findFirst();
            if (bookCategory1.isPresent()) {
                book.getCategories().remove(bookCategory1.get());
            } else {
                throw new BookCategoryConflictException(
                        String.format("Kniha s nazvom %s nepatri pod kategoriu s ID = %s", book.getName(),
                                categoryId));
            }
        }
        return service.updateBookCategories(bookId, book);
    }
}

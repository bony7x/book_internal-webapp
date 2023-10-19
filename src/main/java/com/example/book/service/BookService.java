package com.example.book.service;

import com.example.book.domain.entity.Book;
import com.example.book.domain.service.BookPersistenceService;
import com.example.book.exception.BookNotFoundException;
import com.example.bookCategory.domain.entity.BookCategory;
import com.example.bookCategory.domain.service.BookCategoryPersistenceService;
import com.example.bookCategory.exception.BookCategoryNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class BookService {

    @Inject
    BookPersistenceService service;

    @Inject
    BookCategoryPersistenceService bookCategoryPersistenceService;


    @Transactional
    public Book updateCategories(Integer bookId, Integer[] categoryIds)
            throws BookNotFoundException, BookCategoryNotFoundException {
        log.debug("updateCategories: {} {}", bookId, categoryIds);

        Book book = service.getById(bookId);
        List<BookCategory> all = bookCategoryPersistenceService.getAll();
        List<BookCategory> old = book.getCategories();
        List<BookCategory> newCategories;
        book.setCategories(new ArrayList<>());
        for (Integer integer : categoryIds) {
            BookCategory bookCategory = bookCategoryPersistenceService.getBookCategory(integer);
            book.getCategories().add(bookCategory);
        }
        newCategories = book.getCategories();
        for (BookCategory category : all) {
            if (old.contains(category) && !newCategories.contains(category)) {
                category.setBookCount(category.getBookCount() - 1);
            }
            if (!old.contains(category) && newCategories.contains(category)) {
                category.setBookCount(category.getBookCount() + 1);
            }
        }
        return book;
    }
}

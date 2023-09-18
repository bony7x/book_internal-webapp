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
import java.util.ArrayList;
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
    public Book updateCategories(Integer bookId, Integer[] categoryIds)
            throws BookNotFoundException, BookCategoryNotFoundException {
        log.debug("updateCategories: {} {}", bookId, categoryIds);

        Book book = service.getById(bookId);
        book.setCategories(new ArrayList<>());
        for (Integer integer : categoryIds) {
            BookCategory bookCategory = bookCategoryPersistenceService.getBookCategory(integer);
            book.getCategories().add(bookCategory);
        }
        return book;
    }
}

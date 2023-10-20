package com.example.book.service;

import com.example.book.controller.dto.BooksAndCountDto;
import com.example.book.domain.entity.Book;
import com.example.book.filter.BookFilter;
import com.example.book.domain.repository.BookRepository;
import com.example.book.domain.service.BookPersistenceService;
import com.example.book.exception.BookNotFoundException;
import com.example.bookCategory.domain.entity.BookCategory;
import com.example.bookCategory.domain.service.BookCategoryPersistenceService;
import com.example.bookCategory.exception.BookCategoryNotFoundException;
import com.example.bookCategory.service.BookCategoryService;
import com.example.utils.requests.ExtendedRequest;
import com.example.utils.calculateIndex.CalculateIndex;
import com.example.utils.calculateIndex.Index;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
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
    BookRepository repository;

    @Inject
    BookCategoryPersistenceService bookCategoryPersistenceService;

    @Inject
    BookCategoryService bookCategoryService;

    public BooksAndCountDto getAllBooks(ExtendedRequest request) {
        log.debug("getAllBooks");

        List<Book> sublist;
        BooksAndCountDto filteredBooks;
        filteredBooks = filterBooks(request);
        if (filteredBooks == null) {
            CalculateIndex calculateIndex = new CalculateIndex();
            List<Book> books = request.getSortable().isAscending() ? repository.listAll(
                    Sort.by(request.getSortable().getColumn()).ascending())
                    : repository.listAll(Sort.by(request.getSortable().getColumn()).descending());
            Index indexes = calculateIndex.calculateIndex(request, books.size());
            sublist = books.subList(indexes.getFromIndex(), indexes.getToIndex());
            return new BooksAndCountDto(books.size(), sublist);
        }
        CalculateIndex calculateIndex = new CalculateIndex();
        Index indexes = calculateIndex.calculateIndex(request, filteredBooks.getBooks().size());
        sublist = filteredBooks.getBooks().subList(indexes.getFromIndex(), indexes.getToIndex());
        return new BooksAndCountDto(filteredBooks.getBooks().size(), sublist);
    }


    @Transactional
    public Book updateCategories(Integer bookId, Integer[] categoryIds)
            throws BookNotFoundException, BookCategoryNotFoundException {
        log.debug("updateCategories: {} {}", bookId, categoryIds);

        Book book = service.getById(bookId);
        List<BookCategory> all = bookCategoryService.getAll();
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

    private BooksAndCountDto filterBooks(ExtendedRequest request) {
        log.debug("filterBooks: {}", request);

        ObjectMapper objectMapper = new ObjectMapper();
        if (!request.getFilter().isEmpty()) {
            BookFilter filter = objectMapper.convertValue(request.getFilter(), BookFilter.class);
            List<Book> books;
            if (!filter.getName().isEmpty()) {
                filter.setName("%" + filter.getName().toLowerCase() + "%");
            }
            if (!filter.getAuthor().isEmpty()) {
                filter.setAuthor("%" + filter.getAuthor().toLowerCase() + "%");
            }
            if (!filter.getCategory().isEmpty()) {
                filter.setCategory("%" + filter.getCategory().toLowerCase() + "%");
            }
            if (!filter.getName().isEmpty() && !filter.getAuthor().isEmpty() && !filter.getCategory().isEmpty()) {
                books = repository.list(
                        "Select e from Book e join e.categories c where lower(e.name) like ?1 and lower(e.author) like ?2 and lower(c.name) like ?3",
                        Sort.by("e." + request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getName(),
                        filter.getAuthor(),
                        filter.getCategory());
                return new BooksAndCountDto(books.size(), books);
            }
            if (!filter.getName().isEmpty() && !filter.getAuthor().isEmpty()) {
                books = repository.list("Select e from Book e where lower(e.name) like ?1 and lower(e.author) like ?2",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getName(),
                        filter.getAuthor());
                return new BooksAndCountDto(books.size(), books);
            }
            if (!filter.getName().isEmpty() && !filter.getCategory().isEmpty()) {
                books = repository.list(
                        "Select e from Book e join e.categories c where lower(e.name) like ?1 and lower(c.name) like ?2",
                        Sort.by("e." + request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getName(),
                        filter.getCategory());
                return new BooksAndCountDto(books.size(), books);
            }
            if (!filter.getAuthor().isEmpty() && !filter.getCategory().isEmpty()) {
                books = repository.list(
                        "Select e from Book e join e.categories c where lower(e.author) like ?1 and lower(c.name) like ?2",
                        Sort.by("e." + request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getAuthor(),
                        filter.getCategory());
                return new BooksAndCountDto(books.size(), books);
            }
            if (!filter.getName().isEmpty()) {
                books = repository.list("Select e from Book e where lower(e.name) like ?1",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getName());
                return new BooksAndCountDto(books.size(), books);
            }
            if (!filter.getAuthor().isEmpty()) {
                books = repository.list("Select e from Book e where lower(e.author) like ?1",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getAuthor());
                return new BooksAndCountDto(books.size(), books);
            }
            if (!filter.getCategory().isEmpty()) {
                books = repository.list("Select e from Book e join e.categories c where lower(c.name) like ?1",
                        Sort.by("e." + request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getCategory());
                return new BooksAndCountDto(books.size(), books);
            }
        }
        return null;
    }
}

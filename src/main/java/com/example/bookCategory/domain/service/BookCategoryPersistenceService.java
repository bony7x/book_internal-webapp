package com.example.bookCategory.domain.service;

import com.example.book.domain.entity.Book;
import com.example.book.domain.repository.BookRepository;
import com.example.book.domain.service.BookPersistenceService;
import com.example.book.exception.BookCategoryConflictException;
import com.example.bookCategory.controller.dto.BookCategoriesAndCountDto;
import com.example.bookCategory.domain.entity.BookCategory;
import com.example.bookCategory.domain.entity.BookCategoryFilter;
import com.example.bookCategory.domain.repository.BookCategoryRepository;
import com.example.bookCategory.exception.BookCategoryNotFoundException;
import com.example.request.ExtendedRequest;
import com.example.utils.CalculateIndex.CalculateIndex;
import com.example.utils.CalculateIndex.Index;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
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
        repository.persist(bookCategory);
        return bookCategory;
    }

    public BookCategoriesAndCountDto getAll(ExtendedRequest request) {
        log.debug("getAll: {}", request);

        List<BookCategory> sublist;
        BookCategoriesAndCountDto filteredCategories;
        filteredCategories = filterBookCategories(request);
        if (filteredCategories == null) {
            CalculateIndex calculateIndex = new CalculateIndex();
            List<BookCategory> categories = request.getSortable().isAscending() ? repository.listAll(
                    Sort.by(request.getSortable().getColumn()).ascending())
                    : repository.listAll(Sort.by(request.getSortable().getColumn()).descending());
            Index indexes = calculateIndex.calculateIndex(request, categories.size());
            sublist = categories.subList(indexes.getFromIndex(), indexes.getToIndex());
            return new BookCategoriesAndCountDto(categories.size(), sublist);
        }
        CalculateIndex calculateIndex = new CalculateIndex();
        Index indexes = calculateIndex.calculateIndex(request, filteredCategories.getBookCategories().size());
        sublist = filteredCategories.getBookCategories().subList(indexes.getFromIndex(), indexes.getToIndex());
        return new BookCategoriesAndCountDto(filteredCategories.getBookCategories().size(), sublist);
    }

    public List<BookCategory> getAll() {
        log.debug("getAll");

        return repository.listAll();
    }

    public List<BookCategory> getAllByName(String name) {
        log.debug("getAllByName: {}", name);
        name = "%" + name.toLowerCase() + "%";

        return repository.list("Select e from BookCategory e where lower(e.name) like ?1", name);
    }

    public BookCategory getBookCategory(Integer id) throws BookCategoryNotFoundException {
        log.debug("getBookCategory: {}", id);

        if (id != null) {
            BookCategory bookCategory = repository.findById(Long.valueOf(id));
            if (bookCategory != null) {
                return bookCategory;
            }
            throw new BookCategoryNotFoundException(String.format("Kategoria s id = %s nenajdena", id));
        }
        throw new BookCategoryNotFoundException("ID nemoze byt null");
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

    private BookCategoriesAndCountDto filterBookCategories(ExtendedRequest request) {
        log.debug("filterBookCategories: {}", request);

        ObjectMapper objectMapper = new ObjectMapper();
        if (request.getFilter() != null) {
            BookCategoryFilter filter = objectMapper.convertValue(request.getFilter(), BookCategoryFilter.class);
            List<BookCategory> categories;
            if (filter.getCategory() != null) {
                filter.setCategory("%" + filter.getCategory().toLowerCase() + "%");
                categories = repository.list("Select e from BookCategory e where lower(e.name) like ?1",
                        Sort.by(request.getSortable().getColumn()).direction(
                                request.getSortable().isAscending() ? Direction.Ascending : Direction.Descending),
                        filter.getCategory());
                return new BookCategoriesAndCountDto(categories.size(), categories);
            }
        }
        return null;
    }
}

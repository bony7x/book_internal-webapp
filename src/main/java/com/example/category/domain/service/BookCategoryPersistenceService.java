package com.example.category.domain.service;

import com.example.book.domain.entity.Book;
import com.example.book.domain.repository.BookRepository;
import com.example.book.domain.service.BookPersistenceService;
import com.example.category.controller.dto.BookCategoryDto;
import com.example.category.controller.dto.CreateBookCategoryDto;
import com.example.category.domain.entity.BookCategory;
import com.example.category.domain.repository.BookCategoryRepository;
import com.example.category.exception.BookCategoryNotFoundException;
import com.example.request.ExtendedRequest;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
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
    public BookCategory persist(BookCategory bookCategory) {
        log.debug("persist: {}", bookCategory);

        repository.persist(bookCategory);
        return bookCategory;
    }

    public List<BookCategory> getAll(ExtendedRequest request) {
        log.debug("getAll: {}", request);

        if(request.getSortable().isAscending()){
            return repository.listAll(Sort.by(request.getSortable().getColumn()).ascending());
        } else {
            return repository.listAll(Sort.by(request.getSortable().getColumn()).descending());

        }
    }

    public List<BookCategory> getAll(){
        log.debug("getAll");

        return repository.listAll();
    }

    public List<BookCategory> getAllByName(String name){
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
}

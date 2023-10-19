package com.example.book.domain.service;

import com.example.book.controller.dto.BooksAndCountDto;
import com.example.book.domain.entity.Book;
import com.example.book.domain.entity.BookFilter;
import com.example.book.domain.entity.BookStatus;
import com.example.book.domain.repository.BookRepository;
import com.example.book.exception.BookConflictException;
import com.example.book.exception.BookNotFoundException;
import com.example.bookCategory.domain.entity.BookCategory;
import com.example.borrowing.exception.BorrowingConflictException;
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

package com.example.book.domain.repository;

import com.example.book.domain.entity.Book;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.decorator.Decorator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class BookRepository implements PanacheRepository<Book> {


}

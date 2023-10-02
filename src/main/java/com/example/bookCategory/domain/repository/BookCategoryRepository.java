package com.example.bookCategory.domain.repository;

import com.example.bookCategory.domain.entity.BookCategory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookCategoryRepository implements PanacheRepository<BookCategory> {

}

package com.example.category.domain.repository;

import com.example.category.domain.entity.BookCategory;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BookCategoryRepository implements PanacheRepository<BookCategory> {

}

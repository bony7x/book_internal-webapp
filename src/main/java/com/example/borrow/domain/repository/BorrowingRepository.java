package com.example.borrow.domain.repository;

import com.example.borrow.domain.entity.Borrowing;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BorrowingRepository implements PanacheRepository<Borrowing> {

}

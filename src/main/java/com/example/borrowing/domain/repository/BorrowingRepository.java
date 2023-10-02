package com.example.borrowing.domain.repository;

import com.example.borrowing.domain.entity.Borrowing;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BorrowingRepository implements PanacheRepository<Borrowing> {

}

package com.example.borrow.domain.entity;

import com.example.book.domain.entity.Book;
import com.example.customer.controller.mapper.CustomerMapper;
import com.example.customer.domain.entity.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "brw__borrowing")
public class Borrowing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date_of_borrowing")
    @Temporal(TemporalType.DATE)
    private LocalDate dateOfBorrowing;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private Customer customer;
}

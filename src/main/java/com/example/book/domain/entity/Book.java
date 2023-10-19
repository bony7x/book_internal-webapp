package com.example.book.domain.entity;

import com.example.borrowing.domain.entity.Borrowing;
import com.example.bookCategory.domain.entity.BookCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bk__book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "count")
    private Integer count;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookStatus status;

    @Column(name = "isbn")
    private String isbn;

    @Column(name = "author")
    private String author;

    @ManyToMany(fetch = FetchType.EAGER)
    @ToString.Exclude
    @JoinTable(
            name = "bk__book_categories",
            joinColumns = {@JoinColumn(name = "book_id")},
            inverseJoinColumns = {@JoinColumn(name = "bookcategory_id")}
    )
    private List<BookCategory> categories;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "book")
/*    @JoinTable(
            name = "brw__book_borrowing",
            joinColumns = {@JoinColumn(name = "book_id")},
            inverseJoinColumns = {@JoinColumn(name = "borrowing_id")}
    )*/
    @ToString.Exclude
    private List<Borrowing> borrowings;

    private Integer borrowingCount;
}

package com.example.bookCategory.service;

import com.example.bookCategory.controller.dto.BookCategoriesAndCountDto;
import com.example.bookCategory.domain.entity.BookCategory;
import com.example.bookCategory.domain.repository.BookCategoryRepository;
import com.example.bookCategory.filter.BookCategoryFilter;
import com.example.utils.requests.ExtendedRequest;
import com.example.utils.calculateIndex.CalculateIndex;
import com.example.utils.calculateIndex.Index;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.panache.common.Sort;
import io.quarkus.panache.common.Sort.Direction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class BookCategoryService {

    @Inject
    BookCategoryRepository repository;

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

    private BookCategoriesAndCountDto filterBookCategories(ExtendedRequest request) {
        log.debug("filterBookCategories: {}", request);

        ObjectMapper objectMapper = new ObjectMapper();
        if (!request.getFilter().isEmpty()) {
            BookCategoryFilter filter = objectMapper.convertValue(request.getFilter(), BookCategoryFilter.class);
            List<BookCategory> categories;
            if (!filter.getCategory().isEmpty()) {
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

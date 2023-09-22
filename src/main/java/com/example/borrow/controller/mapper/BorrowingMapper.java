package com.example.borrow.controller.mapper;

import com.example.book.controller.dto.BookDto;
import com.example.book.controller.dto.BookResponse;
import com.example.borrow.controller.dto.BorrowingDto;
import com.example.borrow.controller.dto.BorrowingResponse;
import com.example.borrow.controller.dto.BorrowingsDto;
import com.example.borrow.controller.dto.CreateBorrowingDto;
import com.example.borrow.domain.entity.Borrowing;
import com.example.category.domain.entity.BookCategory;
import com.example.request.ExtendedRequest;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public abstract class BorrowingMapper {

    public abstract Borrowing map(CreateBorrowingDto dto);

    public abstract CreateBorrowingDto map(Borrowing borrowing);

    public BorrowingResponse mapToResponse(List<BorrowingDto> borrowings, ExtendedRequest extendedRequest, Integer size){
        BorrowingResponse borrowingResponse = new BorrowingResponse();
        borrowingResponse.setBorrowings(borrowings);
        borrowingResponse.setPageSize(extendedRequest.getPageable().getPageSize());
        borrowingResponse.setPageNumber(extendedRequest.getPageable().getPageNumber());
        borrowingResponse.setTotalCount(size);
        return borrowingResponse;
    }

    @Mapping(target = "book",source = "borrowing.book")
    @Mapping(target = "customer",source = "borrowing.customer")
    public abstract BorrowingDto mapToDto(Borrowing borrowing);

    public List<Integer> mapCategoryToId(List<BookCategory> value){
        List<Integer> list = new ArrayList<>();
        for (BookCategory b : value){
            list.add(b.getId());
        }
        return list;
    }

    public List<Integer> mapBorrowingToId(List<Borrowing> value){
        List<Integer> list = new ArrayList<>();
        for(Borrowing b : value){
            list.add(b.getId());
        }
        return list;
    }
    public abstract List<BorrowingDto> mapToBDto (List<Borrowing> borrowings);

    public BorrowingsDto map(List<BorrowingDto> list){
        BorrowingsDto dto = new BorrowingsDto();
        dto.setBorrowings(list);
        return dto;
    }
}

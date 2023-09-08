package com.example.borrow.controller.mapper;

import com.example.borrow.controller.dto.BorrowingDto;
import com.example.borrow.controller.dto.BorrowingsDto;
import com.example.borrow.controller.dto.CreateBorrowingDto;
import com.example.borrow.domain.entity.Borrowing;
import com.example.category.domain.entity.BookCategory;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public abstract class BorrowingMapper {

    public abstract Borrowing map(CreateBorrowingDto dto);

    public abstract CreateBorrowingDto map(Borrowing borrowing);

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

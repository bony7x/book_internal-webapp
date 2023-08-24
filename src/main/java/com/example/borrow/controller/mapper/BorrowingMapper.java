package com.example.borrow.controller.mapper;

import com.example.borrow.controller.dto.BorrowingDto;
import com.example.borrow.controller.dto.BorrowingsDto;
import com.example.borrow.controller.dto.CreateBorrowingDto;
import com.example.borrow.domain.entity.Borrowing;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public abstract class BorrowingMapper {

    public abstract Borrowing map(CreateBorrowingDto dto);

    public abstract CreateBorrowingDto map(Borrowing borrowing);

    @Mapping(target = "book",source = "borrowing.book.id")
    @Mapping(target = "customer",source = "borrowing.customer.id")
    public abstract BorrowingDto mapToDto(Borrowing borrowing);

    public abstract List<BorrowingDto> mapToBDto (List<Borrowing> borrowings);

    public BorrowingsDto map(List<BorrowingDto> list){
        BorrowingsDto dto = new BorrowingsDto();
        dto.setBorrowings(list);
        return dto;
    }
}

package com.example.borrowing.controller.mapper;

import com.example.borrowing.controller.dto.BorrowingDto;
import com.example.borrowing.controller.dto.BorrowingResponseDto;
import com.example.borrowing.controller.dto.BorrowingsDto;
import com.example.borrowing.controller.dto.CreateBorrowingDto;
import com.example.borrowing.domain.entity.Borrowing;
import com.example.bookCategory.domain.entity.BookCategory;
import com.example.request.ExtendedRequest;
import java.util.ArrayList;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public abstract class BorrowingMapper {

    public abstract Borrowing map(CreateBorrowingDto dto);

    public abstract CreateBorrowingDto map(Borrowing borrowing);

    public BorrowingResponseDto mapToResponse(List<BorrowingDto> borrowings, ExtendedRequest extendedRequest, Integer size){
        BorrowingResponseDto borrowingResponse = new BorrowingResponseDto();
        borrowingResponse.setBorrowings(borrowings);
        borrowingResponse.setPageSize(extendedRequest.getPageable().getPageSize());
        borrowingResponse.setPageNumber(extendedRequest.getPageable().getPageNumber());
        borrowingResponse.setTotalCount(size);
        return borrowingResponse;
    }

    @Mapping(target = "book",source = "borrowing.book")
    @Mapping(target = "customer",source = "borrowing.customer")
    public abstract BorrowingDto mapToDto(Borrowing borrowing);

    public abstract List<BorrowingDto> mapToDtos(List<Borrowing> borrowings);

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

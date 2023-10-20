package com.example.borrowing.controller.mapper;

import com.example.borrowing.controller.dto.BorrowingDto;
import com.example.borrowing.controller.dto.BorrowingOnlyDto;
import com.example.borrowing.controller.dto.BorrowingResponseDto;
import com.example.borrowing.controller.dto.BorrowingsDto;
import com.example.borrowing.controller.dto.CreateBorrowingDto;
import com.example.borrowing.domain.entity.Borrowing;
import com.example.utils.requests.ExtendedRequest;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public abstract class BorrowingMapper {

    public abstract Borrowing map(CreateBorrowingDto dto);

    public abstract CreateBorrowingDto map(Borrowing borrowing);

    public abstract BorrowingOnlyDto mapToOnlyDto(Borrowing borrowing);

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

    public abstract List<BorrowingDto> mapToBDto (List<Borrowing> borrowings);

    public BorrowingsDto map(List<BorrowingDto> list){
        BorrowingsDto dto = new BorrowingsDto();
        dto.setBorrowings(list);
        return dto;
    }

    public abstract List<BorrowingDto> mapToListDto(List<Borrowing> borrowings);
}

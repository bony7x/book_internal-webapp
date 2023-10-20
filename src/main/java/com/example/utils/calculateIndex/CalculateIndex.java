package com.example.utils.calculateIndex;

import com.example.utils.requests.ExtendedRequest;
import lombok.Data;

@Data
public class CalculateIndex {

    public Index calculateIndex(ExtendedRequest request, Integer size){
        Integer fromIndex = 0;
        Integer toIndex;
        if (request.getPageable().getPageNumber() != 1) {
            fromIndex = (request.getPageable().getPageNumber() - 1) * request.getPageable().getPageSize();
        }
        if (fromIndex + request.getPageable().getPageSize() > size) {
            toIndex = size;
        } else {
            toIndex = fromIndex + request.getPageable().getPageSize();
        }
        return new Index(fromIndex,toIndex);
    }
}

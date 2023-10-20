package com.example.utils.paging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pageable {
    Integer pageNumber;
    Integer pageSize;
}

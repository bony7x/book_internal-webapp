package com.example.utils.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtendedResponse {

    private Integer pageNumber;

    private Integer pageSize;

    private Integer totalCount;
}

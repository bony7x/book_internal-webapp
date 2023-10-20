package com.example.utils.requests;

import com.example.utils.paging.Pageable;
import com.example.utils.sorting.Sortable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtendedRequest {

    Sortable sortable;
    Pageable pageable;
    Map<String, String> filter;
}

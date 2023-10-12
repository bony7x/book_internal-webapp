package com.example.request;

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

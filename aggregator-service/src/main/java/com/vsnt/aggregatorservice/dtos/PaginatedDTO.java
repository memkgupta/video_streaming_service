package com.vsnt.aggregatorservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public  class PaginatedDTO <T>{
    private List<T> data;

    private Integer nextCursor;
    private long totalResults;
    private Integer previousCursor;

}

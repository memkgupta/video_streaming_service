package com.vsnt.channel_service.payload;

import lombok.Data;

import java.util.List;
@Data
public  class PaginatedDTO <T>{
    private List<T> data;

    private Integer nextCursor;
    private long totalResults;
    private Integer previousCursor;

}

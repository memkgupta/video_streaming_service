package com.vsnt.videos_service.dtos;

import lombok.Data;

import java.util.List;
@Data
public class PaginatedResponse<T>{
    private long totalResults;
    private List<T> data;
    private Integer nextCursor;
    private Integer previousCursor;
}

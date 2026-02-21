package com.vsnt.asset_onboarding.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class PageResponseDTO<T> {
    private List<T> data;
    private long total;
    private boolean hasNext;
    private boolean hasPrevious;
    private int page;
}

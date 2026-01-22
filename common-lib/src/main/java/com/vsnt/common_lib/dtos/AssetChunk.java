package com.vsnt.common_lib.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AssetChunk {
    private String assetId;
    private long chunkId;
    private long size;
    private long start ;
    private long end;
    public AssetChunk() {

    }
}

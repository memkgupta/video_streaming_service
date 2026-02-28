package com.vsnt.asset_onboarding.dtos;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Embeddable
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

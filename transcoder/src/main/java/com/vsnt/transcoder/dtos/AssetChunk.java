package com.vsnt.transcoder.dtos;

public class AssetChunk {
    private String assetId;
    private long chunkId;
    private long size;
    private long start ;
    private long end;

    public String getAssetId() {
        return assetId;
    }

    public long getChunkId() {
        return chunkId;
    }

    public long getSize() {
        return size;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }
}

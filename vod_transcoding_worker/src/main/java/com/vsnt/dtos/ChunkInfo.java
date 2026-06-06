package com.vsnt.dtos;

public record ChunkInfo(int start, int end, int chunkNumber) {
    @Override
    public String toString() {
        return String.format("ChunkInfo{start=%ds, end=%ds, chunk=#%03d}", start, end, chunkNumber);
    }
}
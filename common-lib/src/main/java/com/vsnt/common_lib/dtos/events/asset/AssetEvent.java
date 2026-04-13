package com.vsnt.common_lib.dtos.events.asset;

import lombok.Getter;

import java.time.Instant;
@Getter
public abstract class AssetEvent<T> {
    private final AssetEventType eventType;
    protected final String assetId;
    protected final Instant timestamp;
    protected final T data;

    protected AssetEvent(AssetEventType eventType, String assetId, Instant timestamp, T data) {
        this.eventType = eventType;
        this.assetId = assetId;
        this.timestamp = timestamp;
        this.data = data;
    }
}

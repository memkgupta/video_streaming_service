package com.vsnt.dtos;
public class TranscodingSegmentUpdateDTO {
    public TranscodingSegmentUpdateDTO(String assetId, String url, long sequenceNumber, String mediaId, long duration, ResolutionEnum resolution,MediaType mediaType) {
        this.assetId = assetId;
        this.url = url;
        this.mediaType = mediaType;
        this.sequenceNumber = sequenceNumber;
        this.mediaId = mediaId;
        this.duration = duration;
        this.resolution = resolution;
    }

    private String assetId;
    private String url; // url of that particular segment
    private long sequenceNumber;
    private String mediaId;
    private long duration;
    private ResolutionEnum resolution;
    private MediaType mediaType;

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public ResolutionEnum getResolution() {
        return resolution;
    }

    public void setResolution(ResolutionEnum resolution) {
        this.resolution = resolution;
    }
}


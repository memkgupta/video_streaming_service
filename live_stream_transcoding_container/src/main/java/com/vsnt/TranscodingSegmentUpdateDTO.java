package com.vsnt;

public class TranscodingSegmentUpdateDTO {


        private String mediaId;
        private String assetId;
        private long sequenceNumber;
        private String url;
        private long duration;
        private ResolutionEnum resolution;

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

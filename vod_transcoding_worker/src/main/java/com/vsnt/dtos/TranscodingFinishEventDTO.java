package com.vsnt.dtos;

public class TranscodingFinishEventDTO {
    private String mediaId;
    private MediaType mediaType;
//    private boolean completed;
//    private String message;
//    private Timestamp finishedAt;

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }
//
//    public Timestamp getFinishedAt() {
//        return finishedAt;
//    }
//
//    public void setFinishedAt(Timestamp finishedAt) {
//        this.finishedAt = finishedAt;
//    }
}

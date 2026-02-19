package com.vsnt;

public class StreamSegmentUpdateDTO {


        private String streamKey;
        private Long segmentId;
        private String url;
        private Long start;
        private Long end;
        private Long duration;

    public StreamSegmentUpdateDTO() {
    }

    public StreamSegmentUpdateDTO(String streamKey, Long segmentId, String segmentURL, Long start, Long end, Long duration) {
        this.streamKey = streamKey;
        this.segmentId = segmentId;
        this.url = segmentURL;
        this.start = start;
        this.end = end;
        this.duration = duration;
    }

    public String getStreamKey() {
        return streamKey;
    }

    public void setStreamKey(String streamKey) {
        this.streamKey = streamKey;
    }

    public Long getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(Long segmentId) {
        this.segmentId = segmentId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}

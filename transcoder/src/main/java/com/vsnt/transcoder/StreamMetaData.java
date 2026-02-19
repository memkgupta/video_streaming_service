package com.vsnt.transcoder;


import java.sql.Timestamp;

public class StreamMetaData {
private boolean active;
private Timestamp startTime;
private Long segments;
private String streamId;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Long getSegments() {
        return segments;
    }

    public void setSegments(Long segments) {
        this.segments = segments;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }
}

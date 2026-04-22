package com.vsnt.dtos;

public class TranscodeResult {

    public enum Status {
        SUCCESS,
        FAILED,
        PARTIAL_FAILURE,
        STOPPED,        // stopped intentionally (revoke/shutdown)
        INTERRUPTED     // thread interruption
    }

    private final Status status;
    private final String message;
    private final String streamId;

    public TranscodeResult(Status status, String streamId, String message) {
        this.status = status;
        this.streamId = streamId;
        this.message = message;
    }

    public Status getStatus() { return status; }
    public String getMessage() { return message; }
    public String getStreamId() { return streamId; }

    @Override
    public String toString() {
        return "TranscodeResult{" +
                "status=" + status +
                ", streamId='" + streamId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

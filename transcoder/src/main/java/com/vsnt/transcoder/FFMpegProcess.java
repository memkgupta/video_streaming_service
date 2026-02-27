package com.vsnt.transcoder;

public interface FFMpegProcess {
    public boolean stdin(byte[] data , String streamId);
}

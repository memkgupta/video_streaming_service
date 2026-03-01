package com.vsnt;

import com.vsnt.dtos.ResolutionEnum;
import com.vsnt.dtos.TranscodingSegmentUpdateDTO;

import java.nio.file.Path;

public class SegmentEventFactory {

    private final String assetId;
    private final String mediaId;
    private final String cdnBaseUrl;
    private final long segmentDuration;

    public SegmentEventFactory(String assetId,
                               String mediaId,
                               String cdnBaseUrl,
                               long segmentDuration) {
        this.assetId = assetId;
        this.mediaId = mediaId;
        this.cdnBaseUrl = cdnBaseUrl;
        this.segmentDuration = segmentDuration;
    }

    public TranscodingSegmentUpdateDTO generate(Path segmentPath) {
        String fileName = segmentPath.getFileName().toString();
        // Example: segment005.ts → 5
        long sequenceNumber = extractSequenceNumber(fileName);
        // resolution from folder name (360p, 480p etc.)
        String resolutionFolder = segmentPath.getParent().getFileName().toString();
        ResolutionEnum resolution =
                ResolutionEnum.valueOf(resolutionFolder.replace("p", "_P"));
        String url = cdnBaseUrl + "/" + resolutionFolder + "/" + fileName;
        return new TranscodingSegmentUpdateDTO(
                assetId,
                url,
                sequenceNumber,
                mediaId,
                segmentDuration,
                resolution
        );
    }

    private long extractSequenceNumber(String fileName) {
        String number = fileName
                .replace("segment", "")
                .replace(".ts", "");
        return Long.parseLong(number);
    }
}

package com.vsnt.asset_onboarding.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class TranscodedSegment {
    @EmbeddedId
    private TranscodedSegmentId id;
    private String url;
    private String assetId;
    private String mediaId;
    private long sequenceNumber;
    private long start;
    private long end;
    private long duration;
}

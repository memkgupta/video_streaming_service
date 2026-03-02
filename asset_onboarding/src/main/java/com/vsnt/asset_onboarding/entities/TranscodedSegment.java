package com.vsnt.asset_onboarding.entities;

import com.vsnt.asset_onboarding.entities.enums.ResolutionEnum;
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

    private String mediaId;

    private long start;
    @Column(name = "_end")
    private long end;
    private long duration;
}

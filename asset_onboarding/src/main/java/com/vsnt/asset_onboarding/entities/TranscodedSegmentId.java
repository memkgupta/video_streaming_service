package com.vsnt.asset_onboarding.entities;

import com.vsnt.asset_onboarding.entities.enums.ResolutionEnum;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class TranscodedSegmentId implements Serializable {

    private String assetId;
    private long sequenceNumber;
    @Enumerated(EnumType.STRING)
    private ResolutionEnum resolution;
    public TranscodedSegmentId() {}

    public TranscodedSegmentId( String assetId, long sequenceNumber,ResolutionEnum resolution) {

        this.assetId = assetId;
        this.resolution = resolution;
        this.sequenceNumber = sequenceNumber;
    }

    // getters and setters

    // VERY IMPORTANT: equals and hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TranscodedSegmentId that)) return false;
        return sequenceNumber == that.sequenceNumber &&

                Objects.equals(assetId, that.assetId) && resolution == that.resolution;
    }

    @Override
    public int hashCode() {
        return Objects.hash( assetId, sequenceNumber,resolution);
    }
}

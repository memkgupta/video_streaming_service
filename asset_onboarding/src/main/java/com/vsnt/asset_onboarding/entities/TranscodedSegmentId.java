package com.vsnt.asset_onboarding.entities;

import jakarta.persistence.Embeddable;
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

    public TranscodedSegmentId() {}

    public TranscodedSegmentId( String assetId, long sequenceNumber) {

        this.assetId = assetId;
        this.sequenceNumber = sequenceNumber;
    }

    // getters and setters

    // VERY IMPORTANT: equals and hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TranscodedSegmentId)) return false;
        TranscodedSegmentId that = (TranscodedSegmentId) o;
        return sequenceNumber == that.sequenceNumber &&

                Objects.equals(assetId, that.assetId);
    }

    @Override
    public int hashCode() {
        return Objects.hash( assetId, sequenceNumber);
    }
}

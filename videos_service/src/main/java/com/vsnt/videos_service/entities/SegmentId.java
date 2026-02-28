package com.vsnt.videos_service.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Embeddable
@Builder
@Data
@AllArgsConstructor
public class SegmentId {


    private Long number;
private String streamKey;

    public SegmentId() {

    }

    @Override
    public String toString() {
        return streamKey+"-"+number;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SegmentId)) return false;
        SegmentId that = (SegmentId) o;
        return Objects.equals(number, that.number) &&
                Objects.equals(streamKey, that.streamKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, streamKey);
    }
}

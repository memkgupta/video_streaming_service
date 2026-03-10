package com.vsnt.videos_service.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;

import java.util.Objects;

@Embeddable
@AllArgsConstructor
public class ModerationId {
private String videoId;
private String moderationId;

    public ModerationId() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ModerationId that = (ModerationId) o;
        return Objects.equals(videoId, that.videoId) && Objects.equals(moderationId, that.moderationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoId, moderationId);
    }
}

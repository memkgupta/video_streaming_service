package com.vsnt.asset_onboarding.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ModerationStatus {
    APPROVED("approved"),
    FLAGGED("flagged"),
    REJECTED("rejected");

    private final String value;

    ModerationStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static ModerationStatus fromValue(String value) {
        for (ModerationStatus status : ModerationStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ModerationStatus: " + value);
    }
}

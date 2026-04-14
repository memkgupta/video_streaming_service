package com.vsnt.common_lib.dtos.events.live.end;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class LiveEndPayload {
    private long durationSeconds;
    private int peakViewers;
}

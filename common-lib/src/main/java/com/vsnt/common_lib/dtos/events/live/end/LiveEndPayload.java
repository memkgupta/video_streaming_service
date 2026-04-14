package com.vsnt.common_lib.dtos.events.live.end;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiveEndPayload {
    private long durationSeconds;
    private int peakViewers;
}

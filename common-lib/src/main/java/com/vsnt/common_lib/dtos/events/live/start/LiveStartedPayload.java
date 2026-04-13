package com.vsnt.common_lib.dtos.events.live.start;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LiveStartedPayload {
    private String hostUserId;
    private String title;
}

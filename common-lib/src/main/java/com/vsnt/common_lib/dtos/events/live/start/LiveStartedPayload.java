package com.vsnt.common_lib.dtos.events.live.start;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiveStartedPayload {
    private String hostUserId;
    private String title;
}

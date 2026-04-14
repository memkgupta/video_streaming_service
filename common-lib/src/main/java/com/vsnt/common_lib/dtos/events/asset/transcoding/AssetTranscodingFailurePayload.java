package com.vsnt.common_lib.dtos.events.asset.transcoding;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssetTranscodingFailurePayload {
    private String errorMessage;
    private String failedStep;
    private String workerId;
    private String mediaId;
}

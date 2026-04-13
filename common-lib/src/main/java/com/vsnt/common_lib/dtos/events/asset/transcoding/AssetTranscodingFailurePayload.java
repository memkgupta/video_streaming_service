package com.vsnt.common_lib.dtos.events.asset.transcoding;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AssetTranscodingFailurePayload {
    private String errorMessage;
    private String failedStep;
    private String workerId;
}

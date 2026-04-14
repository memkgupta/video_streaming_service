package com.vsnt.common_lib.dtos.events.asset.transcoding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssetTranscodingProgressPayload {
    private double progressPercentage;
}

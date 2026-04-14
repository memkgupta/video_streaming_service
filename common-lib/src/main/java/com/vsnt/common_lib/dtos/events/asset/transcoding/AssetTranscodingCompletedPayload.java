package com.vsnt.common_lib.dtos.events.asset.transcoding;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
@Setter
@NoArgsConstructor
public class AssetTranscodingCompletedPayload {
private String mediaId;
}

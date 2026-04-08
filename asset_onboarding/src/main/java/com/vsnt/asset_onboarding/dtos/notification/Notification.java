package com.vsnt.asset_onboarding.dtos.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification<T> {
    private T message;
    private String orgId;
    private String notificationId;
}

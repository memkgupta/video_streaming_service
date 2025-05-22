package com.vsnt.channel_service.payload.channel.dashboard;

import lombok.Data;

@Data
public class Metric<T> {
    private T currentValue;
    private T previousValue;
    private T decreasePercent;
}

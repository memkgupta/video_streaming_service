package com.vsnt.channel_service.payload.channel;

import com.vsnt.channel_service.payload.channel.dashboard.Metric;
import lombok.Data;

@Data
public class ChannelDashboardDTO {
    private ChannelPayload channelDetails;
    private Metric<Long> views;
    private Metric<Integer> videos;
    private Metric<Long> subscribers;
}

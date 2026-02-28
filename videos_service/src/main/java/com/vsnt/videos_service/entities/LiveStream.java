package com.vsnt.videos_service.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
public class LiveStream {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(unique = true)
    private String streamKey;
    private Timestamp startTime;
    private Timestamp endTime;
    private boolean isLive;
    private String channelId;
}

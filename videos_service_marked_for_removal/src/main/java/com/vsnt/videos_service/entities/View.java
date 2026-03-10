package com.vsnt.videos_service.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class View {
    @Id
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Video video;
    private Timestamp recordedAt;
    private String ipAddress;
}

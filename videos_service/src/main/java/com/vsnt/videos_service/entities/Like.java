package com.vsnt.videos_service.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class Like {
    @Id
    @GeneratedValue
    private long id;
    private String userId;
    private Timestamp createdAt;
    @ManyToOne (fetch = FetchType.LAZY)
    private Video video;
}

package com.vsnt.user.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
public class APIKey {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID accessKey;
    @ManyToOne
    private Group group;
    @ManyToOne
    private Organisation organisation;
    private boolean isValid;
    private String secret;
    private Timestamp createdAt;

}

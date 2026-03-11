package com.vsnt.user.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
public class Group {
@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
private String name;
@ManyToOne
private Organisation organisation;
private boolean isActive;

}

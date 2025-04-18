package com.vsnt.user.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
@Entity
@Data
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String token;
    private String type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private Timestamp created;
    private Timestamp expires;

}

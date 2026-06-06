package com.vsnt.user.entities;


import com.vsnt.user.utils.encryption.EncryptedData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "subscriptions",
        indexes = {
                @Index(name = "idx_org_event", columnList = "orgId,eventType")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String orgId;


    private String eventType;

    private String callbackUrl;

    private String encrypted_secret; // for webhook signature
    private String iv;
    private boolean active;

    private Instant createdAt;
    public EncryptedData getSecret()
    {
        return new EncryptedData(iv, encrypted_secret);
    }
}
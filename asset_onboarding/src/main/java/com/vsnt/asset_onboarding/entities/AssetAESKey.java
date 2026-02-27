package com.vsnt.asset_onboarding.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class AssetAESKey {
@Id
@GeneratedValue
private UUID id;
private String keyURL;
private String assetID;

}

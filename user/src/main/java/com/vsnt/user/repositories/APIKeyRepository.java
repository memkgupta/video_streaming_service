package com.vsnt.user.repositories;

import com.vsnt.user.entities.APIKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface APIKeyRepository extends JpaRepository<APIKey, UUID> {
    Optional<APIKey> findByAccessKey(UUID accessKey);
}

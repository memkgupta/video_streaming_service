package com.vsnt.user.repositories;

import com.vsnt.user.entities.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrganisationRepository extends JpaRepository<Organisation, UUID> {
    public Optional<Organisation> findByAdmin_Id(String admin_id);
}

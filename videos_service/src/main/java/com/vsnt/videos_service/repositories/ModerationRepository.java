package com.vsnt.videos_service.repositories;

import com.vsnt.videos_service.entities.ModerationId;
import com.vsnt.videos_service.entities.ModerationSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationRepository extends JpaRepository<ModerationSummary, ModerationId> {
}

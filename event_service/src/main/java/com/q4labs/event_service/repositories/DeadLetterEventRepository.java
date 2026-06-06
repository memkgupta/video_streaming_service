package com.q4labs.event_service.repositories;

import com.q4labs.event_service.entities.DeadLetterEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeadLetterEventRepository extends JpaRepository<DeadLetterEvent, UUID> {

}

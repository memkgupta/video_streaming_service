package com.q4labs.notification.repositories;

import com.q4labs.notification.entities.DeadLetterEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeadLetterEventRepository extends JpaRepository<DeadLetterEvent, UUID> {

}

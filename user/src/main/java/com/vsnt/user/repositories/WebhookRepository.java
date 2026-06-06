package com.vsnt.user.repositories;
import com.vsnt.user.entities.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
public interface WebhookRepository extends JpaRepository<Webhook,String> , JpaSpecificationExecutor<Webhook> {
    List<Webhook> findByOrgIdAndEventTypeAndActiveTrue(String orgId, String eventType);
    Webhook findByCallbackUrl(String callbackUrl);

    List<Webhook> findByOrgIdAndEventType(String orgId, String eventType);
}


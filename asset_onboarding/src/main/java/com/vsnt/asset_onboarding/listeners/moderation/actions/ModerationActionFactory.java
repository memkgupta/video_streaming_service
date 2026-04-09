package com.vsnt.asset_onboarding.listeners.moderation.actions;

import com.vsnt.asset_onboarding.dtos.ModerationStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ModerationActionFactory {
    private final List<ModerationAction> moderationActions;

    public ModerationActionFactory(List<ModerationAction> moderationActions) {
        this.moderationActions = moderationActions;
    }

    public  ModerationAction getModerationAction(ModerationStatus status){
    return moderationActions.stream().filter(action -> action.support().equals(status)).findFirst().orElse(
            moderationActions.stream().filter(s->s.support().equals(status)).findFirst().orElse(null)
    );

}
}

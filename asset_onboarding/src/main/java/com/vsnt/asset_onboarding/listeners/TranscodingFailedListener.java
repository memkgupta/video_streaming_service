package com.vsnt.asset_onboarding.listeners;

import com.vsnt.asset_onboarding.MessageListener;
import com.vsnt.asset_onboarding.dtos.TranscodingFailedDTO;
import org.springframework.stereotype.Component;

@Component
public class TranscodingFailedListener implements MessageListener<TranscodingFailedDTO> {
    @Override
    public void onMessage(TranscodingFailedDTO message) {

    }
}

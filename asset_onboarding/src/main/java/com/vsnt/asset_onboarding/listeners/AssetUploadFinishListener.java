package com.vsnt.asset_onboarding.listeners;

import com.vsnt.asset_onboarding.entities.Asset;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AssetUploadFinishListener {
    private final List<AssetUploadHandler>  handlers;

    public AssetUploadFinishListener(List<AssetUploadHandler> handlers) {
        this.handlers = handlers;
    }
    public void listen(Asset asset)
    {
    handlers.stream().filter(handler -> handler.supports(asset.getAssetType())).
            findFirst().ifPresent(handler -> handler.handle(asset));
    }
}

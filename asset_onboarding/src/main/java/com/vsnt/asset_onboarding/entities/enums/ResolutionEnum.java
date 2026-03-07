package com.vsnt.asset_onboarding.entities.enums;
public enum ResolutionEnum {

    RESOLUTION_360P(640, 360),
    RESOLUTION_480P(854, 480),
    RESOLUTION_720P(1280, 720),
    RESOLUTION_1080P(1920, 1080),
    RESOLUTION_2160P(3840, 2160);

    private final int width;
    private final int height;

    ResolutionEnum(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String toResolutionString() {
        return height+"p";
    }
}
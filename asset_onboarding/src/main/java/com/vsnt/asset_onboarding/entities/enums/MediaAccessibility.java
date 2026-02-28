package com.vsnt.asset_onboarding.entities.enums;

public enum MediaAccessibility {
    PUBLIC , // any user
    PRIVATE , // users having the MediaPull Key i.e 1000 users
    PROTECTED // users belonging to a particular group can watch this i.e upto 10000 users
}

package com.vsnt.asset_onboarding.config;

import org.springframework.beans.factory.annotation.Value;

public class AppConfig {
    @Value("${app.config.moderation.max_violation_count}" )
    public static final int maxViolationCount = 10;
}

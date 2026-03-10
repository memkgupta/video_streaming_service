package com.vsnt.asset_onboarding;

import com.vsnt.asset_onboarding.dtos.security.SignedCookie;

public interface CDNService {
    byte[] fetch(String url);
    SignedCookie generateCookies();
}

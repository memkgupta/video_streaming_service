package com.vsnt.asset_onboarding.utils;

import com.vsnt.asset_onboarding.dtos.security.SignedCookie;

public interface CookiesService {
    SignedCookie generateCookies();
}

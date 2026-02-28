package com.vsnt.asset_onboarding;

import com.vsnt.asset_onboarding.entities.enums.ResolutionEnum;

import java.util.HashMap;

public  class ResolutionUtil {
    private final HashMap<ResolutionEnum , int[]> hwMap;


    public ResolutionUtil() {
        hwMap = new HashMap<>();
    }

    public int[] getHW(ResolutionEnum resolutionEnum) {
        return hwMap.get(resolutionEnum);
    }
}



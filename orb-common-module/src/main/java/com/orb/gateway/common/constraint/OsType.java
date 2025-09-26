package com.orb.gateway.common.constraint;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum OsType {
    ANDROID, IOS, WINDOW, ETC, UNKNOWN;

    public static OsType getOsType(String osType) {
        return Arrays.stream((OsType.values()))
                .filter(os -> os.name().equalsIgnoreCase(osType))
                .findFirst()
                .orElse(OsType.UNKNOWN);
    }
}

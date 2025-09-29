package com.orb.gateway.auth.common.constraint;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequestHeaderType {
    OS_TYPE("OsType", "운영체제 타입", OsType.ANDROID),
    BUILD_VERSION("BuildVersion", "앱 빌드버전", "1.1.1"),
    APP_UID("AppUid", "앱 고유 ID", "u0_a238"),
    OS_VERSION("OsVersion", "운영체제 버전", "17"),
    DEVICE_MODEL("DeviceModel", "디바이스 모델명", "Galaxy S10");

    private final String headerName;
    private final String headerDesc;
    private final Object sample;
}

package org.example.icatch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceAuthResponse {
    // 수정
    long deviceId;
    String deviceIP;
    long cameraId;
    //수정(수림)
    long userId;
}
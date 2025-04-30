package org.example.icatch.Device;

import lombok.Data;

@Data
public class DeviceAuthRequest {
    private String deviceIp;
    private Long  userId;
}

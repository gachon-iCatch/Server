package org.example.icatch.Device;
import org.example.icatch.enums.DeviceStatus;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceAuthRequest {
    private String deviceIp;
    private Integer deviceId;
    private Integer userId;
    private DeviceStatus deviceStatus;
}

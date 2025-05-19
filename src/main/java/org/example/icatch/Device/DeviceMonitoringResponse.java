package org.example.icatch.Device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.icatch.enums.DeviceStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceMonitoringResponse {
    Integer deviceId;
    String cameraName;
    String userName;
    String deviceIp;
    DeviceStatus deviceStatus;
}
package org.example.icatch.dto;

import lombok.Data;
import org.example.icatch.model.User;

@Data
public class DeviceAuthRequest {
    private String deviceIp;
    private Long userId;
}

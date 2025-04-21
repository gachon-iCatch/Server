package org.example.icatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Integer userId;
    private String nickname;
    private String email;
    private Integer totalDevices;
    private Integer totalCameras;
    private Integer totalTargets;
    private Integer totalGestures;
    private Boolean notificationEnabled;
    private List<DeviceDto> devices;
    private List<CameraDto> cameras;
}


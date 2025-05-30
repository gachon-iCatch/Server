package org.example.icatch.Camera;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CameraDto {
    private Integer cameraId;
    private Integer userId;
    private Integer deviceId;
    private Integer targetId;
    private String cameraName;
    private String isEnabled;
    private Boolean motionDetectionEnabled;
    private String dangerZone;
    private String deviceIp;
}
package org.example.icatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CameraDto {
    private Integer cameraId;
    private String cameraName;
    private String isEnabled;
    private Boolean motionDetectionEnabled;
    private String dangerZone;
    private Integer targetId;
    private String targetType;
}
package org.example.icatch.dto;

public class CameraSetupRequest {
    private Integer userId;
    private Integer deviceId;
    private String cameraName;

    public CameraSetupRequest() {
    }

        this.userId = userId;
        this.deviceId = deviceId;
        this.cameraName = cameraName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Integer deviceId) {
        this.deviceId = deviceId;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    }

    }
}
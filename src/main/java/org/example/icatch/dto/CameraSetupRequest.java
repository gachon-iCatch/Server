package org.example.icatch.dto;

public class CameraSetupRequest {
    private Integer userId;
    private Integer deviceId;
    private String cameraName;
    private String targetType;

    public CameraSetupRequest() {
    }

    public CameraSetupRequest(Integer userId, Integer deviceId, String cameraName, String targetType) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.cameraName = cameraName;
        this.targetType = targetType;
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

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
}
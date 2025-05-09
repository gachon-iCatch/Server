package org.example.icatch.Camera;

public class CameraSetupRequest {
    private Integer userId;
    private Integer deviceId;
    private String cameraName;
    private Integer targetId;
    private String target_Type;

    public CameraSetupRequest() {
    }

    public CameraSetupRequest(Integer userId, Integer deviceId, String cameraName, String target_Type) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.cameraName = cameraName;
        this.target_Type = target_Type;
    }

    public CameraSetupRequest(Integer userId, Integer deviceId, String cameraName, Integer targetId, String target_Type) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.cameraName = cameraName;
        this.targetId = targetId;
        this.target_Type = target_Type;
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

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getTarget_Type() {
        return target_Type;
    }

    public void setTarget_Type(String target_Type) {
        this.target_Type = target_Type;
    }
}
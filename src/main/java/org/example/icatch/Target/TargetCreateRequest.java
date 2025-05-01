package org.example.icatch.Target;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TargetCreateRequest {
    @JsonProperty("userId")
    private Integer userId;

    @JsonProperty("cameraId")
    private Integer cameraId;

    @JsonProperty("cameraName")
    private String cameraName;

    @JsonProperty("targetType")
    private String targetType;

    public TargetCreateRequest() {
    }

    public TargetCreateRequest(Integer userId, Integer cameraId, String cameraName, String targetType) {
        this.userId = userId;
        this.cameraId = cameraId;
        this.cameraName = cameraName;
        this.targetType = targetType;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getCameraId() {
        return cameraId;
    }

    public void setCameraId(Integer cameraId) {
        this.cameraId = cameraId;
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
package org.example.icatch.model;

import jakarta.persistence.*;

@Entity
@Table(name = "camera")
public class Camera{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "camera_id")
    private Integer cameraId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "device_id")
    private Integer deviceId;

    @Column(name = "target_id")
    private Integer targetId;

    @Column(name = "camera_name")
    private String cameraName;

    @Column(name = "is_enabled")
    private String isEnabled; // ENUM('yes', 'no')

    @Column(name = "motion_detection_enabled")
    private Boolean motionDetectionEnabled;

    @Column(name = "danger_zone")
    private String dangerZone;

    public Integer getCameraId() {
        return cameraId;
    }

    public void setCameraId(Integer cameraId) {
        this.cameraId = cameraId;
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

    public Integer getTargetId() {
        return targetId;
    }

    public void setTargetId(Integer targetId) {
        this.targetId = targetId;
    }

    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(String isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Boolean getMotionDetectionEnabled() {
        return motionDetectionEnabled;
    }

    public void setMotionDetectionEnabled(Boolean motionDetectionEnabled) {
        this.motionDetectionEnabled = motionDetectionEnabled;
    }

    public String getDangerZone() {
        return dangerZone;
    }

    public void setDangerZone(String dangerZone) {
        this.dangerZone = dangerZone;
    }
}
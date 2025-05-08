package org.example.icatch.Camera;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.icatch.User.User;
import org.example.icatch.Device.Device;

import java.time.LocalDateTime;


@Entity
@Table(name = "camera")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Camera{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "camera_id")
    private Integer camera_id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", referencedColumnName = "device_id")
    private Device device;

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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Integer getCameraId() {
        return camera_id;
    }

    public void setCameraId(Integer cameraId) {
        this.camera_id = cameraId;
    }


    public Integer getDeviceId() {
        return device != null ? device.getDeviceId() : null;
    }

    public void setDeviceId(Integer deviceId) {
        if (deviceId == null) {
            this.device = null;
            return;
        }

        if (this.device == null) {

            this.device = new Device();
        }
        this.device.setDeviceId(deviceId);
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

    public void setDevice(Device device) {
        this.device = device;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getUserId() {
        return user != null ? user.getUserId() : null;
    }

    public void setUserId(Integer userId) {
        if (userId == null) {
            this.user = null;
            return;
        }

        if (this.user == null) {
            this.user = new User();
        }
        this.user.setUserId(userId);
    }

    public String getDangerZone() {
        return dangerZone;
    }

    public void setDangerZone(String dangerZone) {
        this.dangerZone = dangerZone;
    }
}
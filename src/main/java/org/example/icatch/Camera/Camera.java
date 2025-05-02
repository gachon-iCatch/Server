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
    private User userId;

    @ManyToOne
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

    // Getter/Setter
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
        if (this.device == null) {
            this.device = new Device(); // 기본 생성자 있어야 함
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

    public Integer getUserId() {
        return userId != null ? userId.getUserId() : null;
    }

    public void setUserId(Integer userId) {
        if (this.userId == null) {
            this.userId = new User();
        }
        this.userId.setUserId(userId);
    }
    public String getDangerZone() {
        return dangerZone;
    }

    public void setDangerZone(String dangerZone) {
        this.dangerZone = dangerZone;
    }
}
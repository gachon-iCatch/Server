package org.example.icatch.Picture;

import jakarta.persistence.*;
import org.example.icatch.Device.Device;
import org.example.icatch.User.User;
import org.example.icatch.ActiveLog.ActiveLog;

import java.time.LocalDateTime;

@Entity
@Table(name = "picture")
public class Picture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "device_id")
    private Device device;

    @Column(name = "log_id")
    private Integer logId;

    @Column(name = "image_path")
    private String imagePath;

    @Column(name = "capture_time")
    private LocalDateTime captureTime;

    @Column(name = "storage_order")
    private String storageOrder;

    // Getters and Setters
    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Integer getDeviceId() {
        return device != null ? device.getDeviceId() : null;
    }

    public Integer getLogId() {
        return logId;
    }

    public void setLogId(Integer logId) {
        this.logId = logId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public LocalDateTime getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(LocalDateTime captureTime) {
        this.captureTime = captureTime;
    }

    public String getStorageOrder() {
        return storageOrder;
    }

    public void setStorageOrder(String storageOrder) {
        this.storageOrder = storageOrder;
    }
}
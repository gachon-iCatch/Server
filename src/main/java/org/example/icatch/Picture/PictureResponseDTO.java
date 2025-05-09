package org.example.icatch.Picture;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PictureResponseDTO {
    private Integer imageId;
    private Integer deviceId;
    private String deviceName; // 기기 이름 추가
    private Integer logId;
    private String imagePath;
    private LocalDateTime captureTime;
    private String formattedCaptureTime; // 형식화된 날짜/시간
    private String imageUrl; // 프론트엔드에서 이미지를 로드할 URL

    public PictureResponseDTO(Picture picture) {
        this.imageId = picture.getImageId();
        this.deviceId = picture.getDeviceId();
        this.deviceName = picture.getDevice() != null ? picture.getDevice().getDeviceName() : "알 수 없음";
        this.logId = picture.getLogId();
        this.imagePath = picture.getImagePath();
        this.captureTime = picture.getCaptureTime();

        // 날짜 형식화 (yyyy-MM-dd HH:mm:ss)
        if (captureTime != null) {
            this.formattedCaptureTime = captureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // 이미지 URL 생성
        this.imageUrl = "/api/monitoring/pictures/image/" + picture.getImageId();

    }

    // Getters
    public Integer getImageId() {
        return imageId;
    }

    public Integer getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public Integer getLogId() {
        return logId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public LocalDateTime getCaptureTime() {
        return captureTime;
    }

    public String getFormattedCaptureTime() {
        return formattedCaptureTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }


}
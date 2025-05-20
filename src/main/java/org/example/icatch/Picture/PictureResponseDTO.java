package org.example.icatch.Picture;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PictureResponseDTO {
    private Integer imageId;
    private Integer deviceId;
    private String deviceName;
    private Integer userId;
    private Integer logId;
    private String imagePath;
    private LocalDateTime captureTime;
    private String formattedCaptureTime;
    private String imageUrl;

    public PictureResponseDTO(Picture picture) {
        this.imageId = picture.getImageId();
        this.deviceId = picture.getDevice() != null ? picture.getDeviceId() : null;
        this.deviceName = picture.getDevice() != null ? picture.getDevice().getDeviceName() : "알 수 없음";

        // 이 부분을 수정합니다 - null 체크를 더 안전하게
        this.userId = picture.getDevice() != null && picture.getDevice().getUser() != null ?
                picture.getDevice().getUser().getUserId() : null;

        this.logId = picture.getLogId();
        this.imagePath = picture.getImagePath();
        this.captureTime = picture.getCaptureTime();

        // 날짜 형식화
        if (captureTime != null) {
            this.formattedCaptureTime = captureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        } else {
            this.formattedCaptureTime = null;
        }

        // 이미지 URL 생성 (더 안전하게)
        this.imageUrl = picture.getImageId() != null ?
                "/api/monitoring/pictures/image/" + picture.getImageId() : null;
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
    
    public Integer getUserId() {
        return userId;
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
package org.example.icatch.Picture;

import java.time.LocalDateTime;

public class PictureResponseDTO {
    private Integer imageId;
    private Integer deviceId;
    private Integer logId;
    private String imagePath;
    private LocalDateTime captureTime;
    private String imageUrl; // 프론트엔드에서 이미지를 로드할 URL

    public PictureResponseDTO(Picture picture) {
        this.imageId = picture.getImageId();
        this.deviceId = picture.getDeviceId();
        this.logId = picture.getLogId();
        this.imagePath = picture.getImagePath();
        this.captureTime = picture.getCaptureTime();

        // 이미지 URL 생성 (API 엔드포인트 기반)
        this.imageUrl = "/api/monitoring/pictures/image/" + picture.getImageId();
    }

    // Getters
    public Integer getImageId() {
        return imageId;
    }

    public Integer getDeviceId() {
        return deviceId;
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

    public String getImageUrl() {
        return imageUrl;
    }
}
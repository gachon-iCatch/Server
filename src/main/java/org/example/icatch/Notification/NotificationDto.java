package org.example.icatch.Notification;

import lombok.Data;

@Data
public class NotificationDto {
    private Integer notificationId;
    private Integer userId;
    private Integer cameraId;
    private String notificationType;
    private String title;
    private String createdAt;
}
package org.example.icatch.Notification;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationCreateRequest {
    @NotNull
    private Integer userId;

    @NotNull
    private Integer cameraId;

    @NotNull
    private String notificationType; // "ALERT" 또는 "INFO"

    @NotNull
    private String title;

    @NotNull
    private String createdAt;
}

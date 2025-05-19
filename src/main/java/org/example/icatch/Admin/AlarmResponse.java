package org.example.icatch.Admin;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.example.icatch.Notification.Notification.NotificationType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class AlarmResponse {
    private long UserId;
    private NotificationType notificationType;
    private String title;
    private String createdAt;
}

package org.example.icatch.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private Integer userId;
    private String usernickname;
    private String email;
    private int cameraCount;
    private int targetCount;
    private int gestureCount;
    private boolean notificationEnabled;
}
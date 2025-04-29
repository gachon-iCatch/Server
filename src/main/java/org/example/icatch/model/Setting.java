package org.example.icatch.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "setting")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Integer settingId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "notification_enabled")
    @Enumerated(EnumType.STRING)
    private NotificationEnabled notificationEnabled;

    // 알림 활성화 상태 ENUM
    public enum NotificationEnabled {
        enabled, disabled
    }
}
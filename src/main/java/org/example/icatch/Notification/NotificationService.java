package org.example.icatch.Notification;

import org.example.icatch.User.*;
import org.example.icatch.Camera.Camera;
import org.example.icatch.Camera.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final CameraService cameraService;
    private final SettingService settingService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               UserService userService,
                               CameraService cameraService,
                               SettingService settingService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.cameraService = cameraService;
        this.settingService = settingService;
    }

    // 사용자 알림 목록 조회
    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(Integer userId) {
        List<Notification> notifications = notificationRepository.findByUserUserIdOrderByCreatedAtDesc(userId);

        return notifications.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 사용자 알림 개수 조회
    @Transactional(readOnly = true)
    public int getNotificationCount(Integer userId) {
        return notificationRepository.countByUserId(userId);
    }

    // 새 알림 생성
    @Transactional
    public NotificationDto createNotification(Integer userId, Integer cameraId,
                                              String notificationType, String title,
                                              String createdAt) {
        // 알림 설정 확인
        if (!settingService.isNotificationEnabled(userId)) {
            return null; // 알림이 비활성화되어 있으면 생성하지 않음
        }

        User user = userService.getUserById(userId);
        Camera camera = cameraService.getCameraById(cameraId);

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setCamera(camera);
        notification.setNotificationType(
                Notification.NotificationType.valueOf(notificationType.toUpperCase()));
        notification.setTitle(title);
        notification.setCreatedAt(createdAt);

        notification = notificationRepository.save(notification);
        return convertToDto(notification);
    }

    // 엔티티를 DTO로 변환
    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(notification.getNotificationId());
        dto.setUserId(notification.getUser().getUserId());
        dto.setCameraId(notification.getCamera().getCameraId());
        dto.setNotificationType(notification.getNotificationType().toString());
        dto.setTitle(notification.getTitle());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
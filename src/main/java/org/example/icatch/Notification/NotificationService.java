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

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               UserService userService,
                               CameraService cameraService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.cameraService = cameraService;
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

    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setNotificationId(notification.getNotificationId());
        dto.setUserId(notification.getUser().getUserId());

        // 카메라가 NULL일 수 있으므로 안전하게 처리
        if (notification.getCamera() != null) {
            dto.setCameraId(notification.getCamera().getCameraId());
        } else {
            // 카메라가 삭제된 경우 null 설정
            dto.setCameraId(null);
        }

        dto.setNotificationType(notification.getNotificationType().toString());
        dto.setTitle(notification.getTitle());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
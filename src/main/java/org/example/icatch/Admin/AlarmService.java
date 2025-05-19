package org.example.icatch.Admin;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import org.example.icatch.Admin.AlarmResponse;         
import org.example.icatch.Notification.Notification;        
import org.example.icatch.Notification.NotificationRepository; 
import org.example.icatch.Camera.CameraRepository; 
import org.example.icatch.User.UserRepository;
import org.example.icatch.Notification.Notification.NotificationType;
import org.example.icatch.User.User;
import org.example.icatch.Camera.Camera;

@Service
public class AlarmService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final CameraRepository cameraRepository;

    public AlarmService(NotificationRepository notificationRepository, UserRepository userRepository, CameraRepository cameraRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.cameraRepository = cameraRepository;
    }

    public List<AlarmResponse> getAlarms() {
        List<Notification> notifications = notificationRepository.findAll();
        List<AlarmResponse> alarmResponse = notifications.stream()
                .map(notification -> new AlarmResponse(
                    notification.getUser().getUserId(),
                    notification.getNotificationType(),
                    notification.getTitle(),
                    notification.getCreatedAt()
                ))
                .collect(Collectors.toList());
        return alarmResponse;
    }

    public String postAlarm(AlarmRequest alarmRequest) {
        Camera camera = cameraRepository.findById(alarmRequest.getCameraId()).orElseThrow(() -> new IllegalArgumentException("해당 카메라가 존재하지 않습니다."));
        if (alarmRequest.getUserId() == 0){
            List<User> users = userRepository.findAll();
            for (User user : users) {
                Notification n = new Notification();
                n.setUser(user);
                n.setNotificationType(alarmRequest.getNotificationType());
                n.setTitle(alarmRequest.getTitle());
                n.setCamera(camera);
                n.setCreatedAt(LocalDateTime.now().toString());
                notificationRepository.save(n);
            }
            return alarmRequest.getTitle();
        }
        User user = userRepository.findById(alarmRequest.getUserId()).orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setNotificationType(alarmRequest.getNotificationType());
        notification.setTitle(alarmRequest.getTitle());
        notification.setCamera(camera);
        notification.setCreatedAt(LocalDateTime.now().toString());
        notification = notificationRepository.save(notification);

        return notification.getTitle();

    }
    public void deleteAlarm(Integer userId){
        notificationRepository.deleteById(userId);
    }

}
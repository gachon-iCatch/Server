package org.example.icatch.Notification;

import jakarta.validation.Valid;
import org.example.icatch.User.User;
import org.example.icatch.User.UserService;
import org.example.icatch.security.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationController(NotificationService notificationService,
                                  UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    // 사용자 알림 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse> getUserNotifications() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증된 사용자를 찾을 수 없습니다"));
            }

            List<NotificationDto> notifications = notificationService.getUserNotifications(user.getUserId());

            Map<String, Object> response = new HashMap<>();
            response.put("notifications", notifications);
            response.put("count", notifications.size());

            return ResponseEntity.ok(ApiResponse.success("알림 목록을 성공적으로 조회했습니다", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 알림 개수 조회
    @GetMapping("/count")
    public ResponseEntity<ApiResponse> getNotificationCount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증된 사용자를 찾을 수 없습니다"));
            }

            int count = notificationService.getNotificationCount(user.getUserId());

            return ResponseEntity.ok(ApiResponse.success("알림 개수를 성공적으로 조회했습니다",
                    Map.of("count", count)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // Python 알림 생성 API
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createNotification(@Valid @RequestBody NotificationCreateRequest request) {
        try {
            NotificationDto notification = notificationService.createNotification(
                    request.getUserId(),
                    request.getCameraId(),
                    request.getNotificationType(),
                    request.getTitle(),
                    request.getCreatedAt()
            );

            return ResponseEntity.ok(ApiResponse.success("알림이 성공적으로 생성되었습니다", notification));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
package org.example.icatch.controller;

import jakarta.validation.Valid;
import org.example.icatch.dto.ApiResponse;
import org.example.icatch.dto.PasswordChangeRequest;
import org.example.icatch.dto.UserProfileDto;
import org.example.icatch.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            // 사용자 프로필과 대시보드 정보를 한 번에 가져옴
            UserProfileDto profileDto = userService.getUserProfileData(email);

            return ResponseEntity.ok(ApiResponse.success("사용자 프로필 정보를 성공적으로 조회했습니다", profileDto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/settings")
    public ResponseEntity<ApiResponse> getAccountSettings() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            Map<String, Object> accountSettings = userService.getUserSettingsData(email);

            return ResponseEntity.ok(ApiResponse.success("계정 설정 정보를 성공적으로 조회했습니다", accountSettings));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/notification")
    public ResponseEntity<ApiResponse> toggleNotification(@RequestBody Map<String, Boolean> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            Boolean enabled = request.get("enabled");
            if (enabled == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("enabled 필드가 필요합니다"));
            }

            userService.updateNotificationSetting(email, enabled);
            String message = enabled ? "알림이 활성화되었습니다" : "알림이 비활성화되었습니다";

            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/password")
    public ResponseEntity<ApiResponse> changePassword(
            @Valid @RequestBody PasswordChangeRequest passwordChangeRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            userService.changePassword(email, passwordChangeRequest);
            return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse> deleteAccount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            userService.deleteUser(email);
            return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
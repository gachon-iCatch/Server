package org.example.icatch.User;

import org.example.icatch.Camera.Camera;
import org.example.icatch.Camera.CameraDto;
import org.example.icatch.Camera.CameraService;
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
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;
    private final CameraService cameraService;

    @Autowired
    public ProfileController(UserService userService, CameraService cameraService) {
        this.userService = userService;
        this.cameraService = cameraService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증된 사용자를 찾을 수 없습니다"));
            }

            // 사용자 프로필 정보 조회 (개인정보는 제외)
            Map<String, Object> profileInfo = new HashMap<>();
            profileInfo.put("userId", user.getUserId());
            profileInfo.put("nickname", user.getUserNickname());
            profileInfo.put("email", user.getEmail());

            return ResponseEntity.ok(ApiResponse.success("사용자 프로필 정보를 성공적으로 조회했습니다", profileInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/cameras")
    public ResponseEntity<ApiResponse> getUserCameras() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            List<CameraDto> cameras = cameraService.getCamerasByUserEmail(email);

            // 카메라 IP 주소 등 민감한 정보 마스킹 처리
            for (CameraDto camera : cameras) {
                // 여기서 필요한 데이터 가공 처리
                // 예: IP 주소 마스킹, 연결 상태 체크 등
            }

            return ResponseEntity.ok(ApiResponse.success("사용자 카메라 목록을 성공적으로 조회했습니다", cameras));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/cameras/{cameraId}")
    public ResponseEntity<ApiResponse> deleteCamera(@PathVariable Integer cameraId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증된 사용자를 찾을 수 없습니다"));
            }

            // 카메라 삭제 처리
            boolean deleted = cameraService.deleteCamera(cameraId, user.getUserId());

            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("카메라가 성공적으로 삭제되었습니다"));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.error("카메라 삭제에 실패했습니다"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/cameras/count")
    public ResponseEntity<ApiResponse> getCameraCount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증된 사용자를 찾을 수 없습니다"));
            }

            int count = cameraService.countCamerasByUserId(user.getUserId());

            return ResponseEntity.ok(ApiResponse.success("사용자 카메라 개수를 성공적으로 조회했습니다",
                    Map.of("count", count)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
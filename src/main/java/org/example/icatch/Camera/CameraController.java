package org.example.icatch.Camera;

import org.example.icatch.security.ApiResponse;
import org.example.icatch.User.User;
import org.example.icatch.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cameras")
public class CameraController {

    private final CameraService cameraService;
    private final UserService userService;

    @Autowired
    public CameraController(CameraService cameraService, UserService userService) {
        this.cameraService = cameraService;
        this.userService = userService;
    }


     //모든 카메라 목록 조회

    @GetMapping("/user")
    public ResponseEntity<ApiResponse> getUserCameras() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            List<CameraDto> cameras = cameraService.getCamerasByUserEmail(email);
            return ResponseEntity.ok(ApiResponse.success("카메라 목록을 성공적으로 조회했습니다", cameras));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    //특정 카메라 정보 조회

    @GetMapping("/{cameraId}")
    public ResponseEntity<ApiResponse> getCameraById(@PathVariable Integer cameraId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            CameraDto camera = cameraService.getCameraByIdAndUserEmail(cameraId, email);
            return ResponseEntity.ok(ApiResponse.success("카메라 정보를 성공적으로 조회했습니다", camera));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    //카메라 스트림 URL 조회

    @GetMapping("/{cameraId}/stream")
    public ResponseEntity<ApiResponse> getCameraStream(@PathVariable Integer cameraId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            Map<String, String> streamInfo = cameraService.getCameraStreamInfo(cameraId, email);
            return ResponseEntity.ok(ApiResponse.success("카메라 스트림 정보를 성공적으로 조회했습니다", streamInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }


      //카메라 방향 제어
    @PostMapping("/{cameraId}/control")
    public ResponseEntity<ApiResponse> controlCamera(
            @PathVariable Integer cameraId,
            @RequestBody Map<String, String> direction) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            String directionValue = direction.get("direction");
            if (directionValue == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("direction 필드가 필요합니다"));
            }

            boolean result = cameraService.controlCamera(cameraId, email, directionValue);
            return ResponseEntity.ok(ApiResponse.success("카메라 방향이 성공적으로 제어되었습니다", Map.of("success", result)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/setup")
    public ResponseEntity<ApiResponse> setupCamera(
            @RequestParam Integer deviceId,
            @RequestParam Integer targetId,
            @RequestParam String cameraName) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증된 사용자를 찾을 수 없습니다"));
            }

            Integer userId = user.getUserId();
            Integer cameraId = cameraService.setupCamera(userId, deviceId, targetId, cameraName);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("카메라가 성공적으로 설정되었습니다", Map.of("cameraId", cameraId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{cameraId}/danger-zone")
    public ResponseEntity<ApiResponse> setDangerZone(
            @PathVariable Integer cameraId,
            @RequestBody List<Integer> zoneNumbers) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증된 사용자를 찾을 수 없습니다"));
            }

            Camera camera = cameraService.setDangerZone(cameraId, user.getUserId(), zoneNumbers);
            return ResponseEntity.ok(ApiResponse.success("위험 영역이 성공적으로 설정되었습니다", camera));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{cameraId}/danger-zone")
    public ResponseEntity<ApiResponse> getDangerZone(@PathVariable Integer cameraId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증된 사용자를 찾을 수 없습니다"));
            }

            String dangerZone = cameraService.getDangerZone(cameraId, user.getUserId());
            return ResponseEntity.ok(ApiResponse.success("위험 영역 정보를 성공적으로 조회했습니다", Map.of("dangerZone", dangerZone)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{cameraId}")
    public ResponseEntity<ApiResponse> deleteCamera(@PathVariable Integer cameraId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            User user = userService.getUserByEmail(email);

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증된 사용자를 찾을 수 없습니다"));
            }

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
}
package org.example.icatch.controller;

import org.example.icatch.dto.ApiResponse;
import org.example.icatch.dto.CameraDto;
import org.example.icatch.model.Camera;
import org.example.icatch.service.CameraService;
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

    @Autowired
    public CameraController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

    /**
     * 사용자의 모든 카메라 목록 조회
     */
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

    /**
     * 특정 카메라 정보 조회
     */
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

    /**
     * 카메라 스트림 URL 조회
     */
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

    /**
     * 카메라 방향 제어
     */
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
    public ResponseEntity<Map<String, Integer>> setupCamera(
            @RequestParam Integer userId,
            @RequestParam Integer deviceId,
            @RequestParam Integer targetId,
            @RequestParam String cameraName) {

        Integer cameraId = cameraService.setupCamera(userId, deviceId, targetId, cameraName);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("cameraId", cameraId));
    }

    @PostMapping("/{cameraId}/danger-zone")
    public ResponseEntity<Camera> setDangerZone(
            @PathVariable Integer cameraId,
            @RequestParam Integer userId,
            @RequestBody List<Integer> zoneNumbers) {

        Camera camera = cameraService.setDangerZone(cameraId, userId, zoneNumbers);
        return ResponseEntity.ok(camera);
    }

    @GetMapping("/{cameraId}/danger-zone")
    public ResponseEntity<Map<String, String>> getDangerZone(
            @PathVariable Integer cameraId,
            @RequestParam Integer userId) {

        String dangerZone = cameraService.getDangerZone(cameraId, userId);
        return ResponseEntity.ok(Map.of("dangerZone", dangerZone));
    }
}
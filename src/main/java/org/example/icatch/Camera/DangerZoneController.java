package org.example.icatch.Camera;

import org.example.icatch.security.ApiResponse;
import org.example.icatch.User.User;
import org.example.icatch.User.UserRepository;
import org.example.icatch.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/camera")
public class DangerZoneController {

    private final CameraService cameraService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Autowired
    public DangerZoneController(CameraService cameraService,
                                JwtTokenProvider jwtTokenProvider,
                                UserRepository userRepository) {
        this.cameraService = cameraService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }


    @PostMapping("/{cameraId}/danger-zone")
    public ResponseEntity<ApiResponse> setDangerZone(
            @PathVariable Integer cameraId,
            @RequestBody DangerZoneRequest request,
            @RequestHeader("Authorization") String token) {

        try {
            String email = jwtTokenProvider.getEmailFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
            Integer userId = user.getUserId();

            Camera camera = cameraService.setDangerZone(cameraId, userId, request.getZones());

            return ResponseEntity.ok(ApiResponse.success("위험 구역이 성공적으로 설정되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("위험 구역 설정 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/{cameraId}/danger-zone")
    public ResponseEntity<ApiResponse> getDangerZone(
            @PathVariable Integer cameraId,
            @RequestHeader("Authorization") String token) {

        try {
            String email = jwtTokenProvider.getEmailFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
            Integer userId = user.getUserId();

            String dangerZoneStr = cameraService.getDangerZone(cameraId, userId);

            List<Integer> zones = new ArrayList<>();
            if (dangerZoneStr != null && !dangerZoneStr.isEmpty()) {
                zones = Arrays.stream(dangerZoneStr.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            }

            return ResponseEntity.ok(ApiResponse.success("위험 구역 정보 조회 성공", zones));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("위험 구역 정보 조회 실패: " + e.getMessage()));
        }
    }

    @GetMapping("/{cameraId}/grid")
    public ResponseEntity<ApiResponse> getCameraGrid(
            @PathVariable Integer cameraId,
            @RequestHeader("Authorization") String token) {

        try {
            String email = jwtTokenProvider.getEmailFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
            Integer userId = user.getUserId();

            String dangerZoneStr = cameraService.getDangerZone(cameraId, userId);

            List<Integer> selectedZones = new ArrayList<>();
            if (dangerZoneStr != null && !dangerZoneStr.isEmpty()) {
                selectedZones = Arrays.stream(dangerZoneStr.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            }

            List<GridZone> gridZones = new ArrayList<>();
            for (int i = 1; i <= 9; i++) {
                GridZone zone = new GridZone();
                zone.setZoneId(i);
                zone.setSelected(selectedZones.contains(i));
                gridZones.add(zone);
            }

            return ResponseEntity.ok(ApiResponse.success("카메라 그리드 정보를 성공적으로 가져왔습니다", gridZones));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("카메라 그리드 정보를 가져오는데 실패했습니다: " + e.getMessage()));
        }
    }

    private static class GridZone {
        private int zoneId;
        private boolean selected;

        public int getZoneId() {
            return zoneId;
        }

        public void setZoneId(int zoneId) {
            this.zoneId = zoneId;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}
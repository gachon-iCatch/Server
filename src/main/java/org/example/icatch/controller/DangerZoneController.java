package org.example.icatch.controller;

import org.example.icatch.dto.ApiResponse;
import org.example.icatch.dto.DangerZoneRequest;
import org.example.icatch.model.Camera;
import org.example.icatch.model.User;
import org.example.icatch.repository.UserRepository;
import org.example.icatch.security.JwtTokenProvider;
import org.example.icatch.service.CameraService;
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

    /**
     * 위험 구역 설정 API
     */
    @PostMapping("/{cameraId}/danger-zone")
    public ResponseEntity<ApiResponse> setDangerZone(
            @PathVariable Integer cameraId,
            @RequestBody DangerZoneRequest request,
            @RequestHeader("Authorization") String token) {

        try {
            // 토큰에서 사용자 정보 추출
            String email = jwtTokenProvider.getEmailFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
            Integer userId = user.getUserId();

            // 위험 구역 설정
            Camera camera = cameraService.setDangerZone(cameraId, userId, request.getZones());

            // 응답 생성 (정적 팩토리 메서드 사용)
            return ResponseEntity.ok(ApiResponse.success("위험 구역이 성공적으로 설정되었습니다."));
        } catch (Exception e) {
            // 오류 응답 생성 (정적 팩토리 메서드 사용)
            return ResponseEntity.badRequest().body(ApiResponse.error("위험 구역 설정 실패: " + e.getMessage()));
        }
    }

    /**
     * 위험 구역 조회 API
     */
    @GetMapping("/{cameraId}/danger-zone")
    public ResponseEntity<ApiResponse> getDangerZone(
            @PathVariable Integer cameraId,
            @RequestHeader("Authorization") String token) {

        try {
            // 토큰에서 사용자 정보 추출
            String email = jwtTokenProvider.getEmailFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
            Integer userId = user.getUserId();

            // 위험 구역 정보 조회
            String dangerZoneStr = cameraService.getDangerZone(cameraId, userId);

            // 문자열을 리스트로 변환
            List<Integer> zones = new ArrayList<>();
            if (dangerZoneStr != null && !dangerZoneStr.isEmpty()) {
                zones = Arrays.stream(dangerZoneStr.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            }

            // 응답 생성 (정적 팩토리 메서드 사용)
            return ResponseEntity.ok(ApiResponse.success("위험 구역 정보 조회 성공", zones));
        } catch (Exception e) {
            // 오류 응답 생성 (정적 팩토리 메서드 사용)
            return ResponseEntity.badRequest().body(ApiResponse.error("위험 구역 정보 조회 실패: " + e.getMessage()));
        }
    }

    /**
     * 카메라 그리드 정보 제공 API (9개 구역에 대한 정보)
     */
    @GetMapping("/{cameraId}/grid")
    public ResponseEntity<ApiResponse> getCameraGrid(
            @PathVariable Integer cameraId,
            @RequestHeader("Authorization") String token) {

        try {
            // 토큰에서 사용자 정보 추출
            String email = jwtTokenProvider.getEmailFromToken(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
            Integer userId = user.getUserId();

            // 현재 위험 구역 정보 조회
            String dangerZoneStr = cameraService.getDangerZone(cameraId, userId);

            // 선택된 구역 목록 생성
            List<Integer> selectedZones = new ArrayList<>();
            if (dangerZoneStr != null && !dangerZoneStr.isEmpty()) {
                selectedZones = Arrays.stream(dangerZoneStr.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
            }

            // 9개 구역에 대한 정보 생성
            List<GridZone> gridZones = new ArrayList<>();
            for (int i = 1; i <= 9; i++) {
                GridZone zone = new GridZone();
                zone.setZoneId(i);
                zone.setSelected(selectedZones.contains(i));
                gridZones.add(zone);
            }

            // 응답 생성 (정적 팩토리 메서드 사용)
            return ResponseEntity.ok(ApiResponse.success("카메라 그리드 정보를 성공적으로 가져왔습니다", gridZones));
        } catch (Exception e) {
            // 오류 응답 생성 (정적 팩토리 메서드 사용)
            return ResponseEntity.badRequest().body(ApiResponse.error("카메라 그리드 정보를 가져오는데 실패했습니다: " + e.getMessage()));
        }
    }

    /**
     * 그리드 구역 정보를 위한 내부 클래스
     */
    private static class GridZone {
        private int zoneId;      // 구역 ID (1-9)
        private boolean selected; // 선택 여부

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
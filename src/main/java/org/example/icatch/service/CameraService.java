package org.example.icatch.service;

import org.example.icatch.dto.CameraDto;
import org.example.icatch.model.Camera;
import org.example.icatch.model.User;
import org.example.icatch.repository.CameraRepository;
import org.example.icatch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CameraService {

    private final CameraRepository cameraRepository;
    private final UserRepository userRepository;

    @Autowired
    public CameraService(CameraRepository cameraRepository, UserRepository userRepository) {
        this.cameraRepository = cameraRepository;
        this.userRepository = userRepository;
    }

    /**
     * 위험 구역 설정 (한 개만 선택 가능)
     * @param cameraId 카메라 ID
     * @param userId 사용자 ID
     * @param zoneNumbers 선택된 구역 번호 리스트
     */
    @Transactional
    public Camera setDangerZone(Integer cameraId, Integer userId, List<Integer> zoneNumbers) {
        // 카메라 조회
        Optional<Camera> optionalCamera = cameraRepository.findById(cameraId);
        if (!optionalCamera.isPresent()) {
            throw new RuntimeException("해당 카메라를 찾을 수 없습니다: " + cameraId);
        }

        Camera camera = optionalCamera.get();

        // 카메라 소유자 확인
        if (!camera.getUserId().equals(userId)) {
            throw new RuntimeException("해당 카메라에 대한 접근 권한이 없습니다");
        }

        // 위험 구역 선탹 개수 제한 (1개만)
        if (zoneNumbers.size() > 1) {
            throw new IllegalArgumentException("위험 구역은 하나만 선택할 수 있습니다");
        }

        // 구역 번호 유효성 검사
        validateZoneNumbers(zoneNumbers);

        // 선택된 구역 번호를 문자열로 변환
        String dangerZoneStr = "";
        if (!zoneNumbers.isEmpty()) {
            dangerZoneStr = String.valueOf(zoneNumbers.get(0));
        }

        // 위험 구역 설정
        camera.setDangerZone(dangerZoneStr);

        // 저장 및 반환
        return cameraRepository.save(camera);
    }

    /**
     * 위험 구역 정보 조회
     */
    @Transactional(readOnly = true)
    public String getDangerZone(Integer cameraId, Integer userId) {
        Optional<Camera> optionalCamera = cameraRepository.findById(cameraId);
        if (!optionalCamera.isPresent()) {
            throw new RuntimeException("해당 카메라를 찾을 수 없습니다: " + cameraId);
        }

        Camera camera = optionalCamera.get();

        // 카메라 소유자 확인
        if (!camera.getUserId().equals(userId)) {
            throw new RuntimeException("해당 카메라에 대한 접근 권한이 없습니다");
        }

        return camera.getDangerZone();
    }

    @Transactional
    public Integer setupCamera(Integer userId, Integer deviceId, Integer targetId, String cameraName) {
        Camera camera = new Camera();
        camera.setUserId(userId);
        camera.setDeviceId(deviceId);
        camera.setTargetId(targetId);
        camera.setCameraName(cameraName);
        camera.setIsEnabled("yes");
        camera.setMotionDetectionEnabled(true);

        Camera savedCamera = cameraRepository.save(camera);
        return savedCamera.getCameraId();
    }
    /**
     * 구역 번호 유효성 검사 (1-9 범위 내 확인)
     */
    private void validateZoneNumbers(List<Integer> zoneNumbers) {
        if (zoneNumbers == null || zoneNumbers.isEmpty()) {
            return; // 빈 리스트는 허용 (선택 영역 없음)
        }

        for (Integer zoneNumber : zoneNumbers) {
            if (zoneNumber < 1 || zoneNumber > 9) {
                throw new IllegalArgumentException("유효하지 않은 구역 번호입니다.");
            }
        }
    }


    /**
     * 사용자 이메일로 카메라 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CameraDto> getCamerasByUserEmail(String email) {
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        // 사용자의 카메라 목록 조회
        List<Camera> cameras = cameraRepository.findByUserId_UserId(user.getUserId());

        // DTO로 변환하여 반환
        return cameras.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 카메라 ID와 사용자 이메일로 특정 카메라 조회
     */
    @Transactional(readOnly = true)
    public CameraDto getCameraByIdAndUserEmail(Integer cameraId, String email) {
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        // 카메라 조회
        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new IllegalArgumentException("카메라를 찾을 수 없습니다: " + cameraId));

        // 소유자 확인
        if (!camera.getUserId().equals(user.getUserId())) {
            throw new RuntimeException("해당 카메라에 대한 접근 권한이 없습니다");
        }

        // DTO로 변환하여 반환
        return convertToDto(camera);
    }

    /**
     * 카메라 스트림 정보 조회
     */
    @Transactional(readOnly = true)
    public Map<String, String> getCameraStreamInfo(Integer cameraId, String email) {
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        // 카메라 조회
        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new IllegalArgumentException("카메라를 찾을 수 없습니다: " + cameraId));

        // 소유자 확인
        if (!camera.getUserId().equals(user.getUserId())) {
            throw new RuntimeException("해당 카메라에 대한 접근 권한이 없습니다");
        }

        // 스트림 URL 생성 (여기서는 예시로 단순한 URL을 반환)
        String streamUrl = "rtsp://stream.example.com/camera/" + cameraId;

        return Map.of(
                "streamUrl", streamUrl,
                "cameraId", cameraId.toString(),
                "cameraName", camera.getCameraName()
        );
    }

    /**
     * 카메라 방향 제어
     */
    @Transactional
    public boolean controlCamera(Integer cameraId, String email, String direction) {
        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        // 카메라 조회
        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new IllegalArgumentException("카메라를 찾을 수 없습니다: " + cameraId));

        // 소유자 확인
        if (!camera.getUserId().equals(user.getUserId())) {
            throw new RuntimeException("해당 카메라에 대한 접근 권한이 없습니다");
        }

        // 방향 유효성 검사
        if (!isValidDirection(direction)) {
            throw new IllegalArgumentException("유효하지 않은 방향입니다: " + direction);
        }

        // 여기서 실제 카메라 제어 로직을 구현합니다.
        // 이 예시에서는 방향 제어가 성공했다고 가정하고 true를 반환합니다.

        return true;
    }

    /**
     * 방향 유효성 검사
     */
    private boolean isValidDirection(String direction) {
        // 유효한 방향: up, down, left, right
        return direction != null &&
                (direction.equals("up") ||
                        direction.equals("down") ||
                        direction.equals("left") ||
                        direction.equals("right"));
    }

    /**
     * Camera 엔티티를 CameraDto로 변환
     */
    private CameraDto convertToDto(Camera camera) {
        CameraDto dto = new CameraDto();
        dto.setCameraId(camera.getCameraId());
        dto.setCameraName(camera.getCameraName());
        dto.setUserId(camera.getUserId());
        dto.setDeviceId(camera.getDeviceId());
        dto.setTargetId(camera.getTargetId());
        dto.setIsEnabled(camera.getIsEnabled());
        dto.setMotionDetectionEnabled(camera.getMotionDetectionEnabled());
        dto.setDangerZone(camera.getDangerZone());
        return dto;
    }



}
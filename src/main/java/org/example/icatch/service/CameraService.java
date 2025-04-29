package org.example.icatch.service;

import org.example.icatch.dto.CameraDto;
import org.example.icatch.model.Camera;
import org.example.icatch.model.Target;
import org.example.icatch.model.User;
import org.example.icatch.repository.CameraRepository;
import org.example.icatch.repository.TargetRepository;
import org.example.icatch.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CameraService {

    private final CameraRepository cameraRepository;
    private final UserRepository userRepository;
    private final TargetRepository targetRepository;

    @Autowired
    public CameraService(CameraRepository cameraRepository, UserRepository userRepository, TargetRepository targetRepository) {
        this.cameraRepository = cameraRepository;
        this.userRepository = userRepository;
        this.targetRepository = targetRepository;
    }

    /**
     * 사용자 이메일로 카메라 목록 조회
     */
    @Transactional(readOnly = true)
    public List<CameraDto> getCamerasByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        List<Camera> cameras = cameraRepository.findByUserId_UserId(user.getUserId());
        return cameras.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 카메라 정보 조회 (권한 체크 포함)
     */
    @Transactional(readOnly = true)
    public CameraDto getCameraByIdAndUserEmail(Integer cameraId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new RuntimeException("카메라를 찾을 수 없습니다"));

        // 사용자 권한 확인
        if (!camera.getUserId().equals(user.getUserId())) {
            throw new RuntimeException("해당 카메라에 대한 접근 권한이 없습니다");
        }

        return convertToDto(camera);
    }

    /**
     * 카메라 스트림 정보 조회
     */
    @Transactional(readOnly = true)
    public Map<String, String> getCameraStreamInfo(Integer cameraId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new RuntimeException("카메라를 찾을 수 없습니다"));

        // 사용자 권한 확인
        if (!camera.getUserId().equals(user.getUserId())) {
            throw new RuntimeException("해당 카메라에 대한 접근 권한이 없습니다");
        }

        // 스트림 정보 생성 (실제 구현에서는 디바이스에서 스트림 URL을 가져와야 함)
        Map<String, String> streamInfo = new HashMap<>();
        streamInfo.put("streamUrl", "/api/stream/" + cameraId);
        streamInfo.put("thumbnailUrl", "/api/thumbnails/" + cameraId);
        streamInfo.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        return streamInfo;
    }

    /**
     * 카메라 방향 제어
     */
    @Transactional
    public boolean controlCamera(Integer cameraId, String email, String direction) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));

        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new RuntimeException("카메라를 찾을 수 없습니다"));

        // 사용자 권한 확인
        if (!camera.getUserId().equals(user.getUserId())) {
            throw new RuntimeException("해당 카메라에 대한 접근 권한이 없습니다");
        }

        // 방향 유효성 검사
        if (!isValidDirection(direction)) {
            throw new IllegalArgumentException("유효하지 않은 방향입니다: " + direction);
        }

        // 실제 카메라 제어 로직은 디바이스와 통신하는 코드가 필요함
        // 여기서는 항상 성공한다고 가정
        return true;
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
     * 방향 값 유효성 검사
     */
    private boolean isValidDirection(String direction) {
        return Arrays.asList("up", "down", "left", "right", "center").contains(direction.toLowerCase());
    }

    /**
     * Camera 엔티티를 DTO로 변환
     */
    private CameraDto convertToDto(Camera camera) {
        CameraDto dto = new CameraDto();
        dto.setCameraId(camera.getCameraId());
        dto.setCameraName(camera.getCameraName());
        dto.setIsEnabled(camera.getIsEnabled());
        dto.setMotionDetectionEnabled(camera.getMotionDetectionEnabled());
        dto.setDangerZone(camera.getDangerZone());

        // 타겟 정보 설정
        Integer targetId = camera.getTargetId();
        dto.setTargetId(targetId);

        if (targetId != null) {
            Optional<Target> targetOpt = targetRepository.findById(targetId);
            if (targetOpt.isPresent()) {
                dto.setTargetType(String.valueOf(targetOpt.get().getTargetType()));
            }
        }

        return dto;
    }

    public int countByUserId(Integer userId) {
        return cameraRepository.countByUserId_UserId(userId);
    }

}
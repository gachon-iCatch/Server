package org.example.icatch.service;

import org.example.icatch.model.Camera;
import org.example.icatch.repository.CameraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CameraService {

    private final CameraRepository cameraRepository;

    @Autowired
    public CameraService(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
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

}
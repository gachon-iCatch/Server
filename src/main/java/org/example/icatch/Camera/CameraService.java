package org.example.icatch.Camera;

import org.example.icatch.User.User;
import org.example.icatch.User.UserRepository;
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

    @Transactional
    public Camera setDangerZone(Integer cameraId, Integer userId, List<Integer> zoneNumbers) {
        Optional<Camera> optionalCamera = cameraRepository.findById(cameraId);
        if (!optionalCamera.isPresent()) {
            throw new RuntimeException("해당 카메라를 찾을 수 없습니다: " + cameraId);
        }

        Camera camera = optionalCamera.get();

        if (!camera.getUserId().equals(userId)) {
            throw new RuntimeException("해당 카메라에 대한 접근 권한이 없습니다");
        }

        if (zoneNumbers.size() > 1) {
            throw new IllegalArgumentException("위험 구역은 하나만 선택할 수 있습니다");
        }

        validateZoneNumbers(zoneNumbers);

        String dangerZoneStr = "";
        if (!zoneNumbers.isEmpty()) {
            dangerZoneStr = String.valueOf(zoneNumbers.get(0));
        }

        camera.setDangerZone(dangerZoneStr);

        return cameraRepository.save(camera);
    }


    @Transactional(readOnly = true)
    public String getDangerZone(Integer cameraId, Integer userId) {
        Optional<Camera> optionalCamera = cameraRepository.findById(cameraId);
        if (!optionalCamera.isPresent()) {
            throw new RuntimeException("해당 카메라를 찾을 수 없습니다: " + cameraId);
        }

        Camera camera = optionalCamera.get();

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

    private void validateZoneNumbers(List<Integer> zoneNumbers) {
        if (zoneNumbers == null || zoneNumbers.isEmpty()) {
            return;
        }

        for (Integer zoneNumber : zoneNumbers) {
            if (zoneNumber < 1 || zoneNumber > 9) {
                throw new IllegalArgumentException("유효하지 않은 구역 번호입니다.");
            }
        }
    }



    @Transactional(readOnly = true)
    public List<CameraDto> getCamerasByUserEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        List<Camera> cameras = cameraRepository.findByUserId_UserId(user.getUserId());

        return cameras.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public CameraDto getCameraByIdAndUserEmail(Integer cameraId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new IllegalArgumentException("카메라를 찾을 수 없습니다: " + cameraId));

        if (!camera.getUserId().equals(user.getUserId())) {
            throw new RuntimeException("해당 카메라에 대한 접근 권한이 없습니다");
        }

        return convertToDto(camera);
    }


    @Transactional(readOnly = true)
    public Map<String, String> getCameraStreamInfo(Integer cameraId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new IllegalArgumentException("카메라를 찾을 수 없습니다: " + cameraId));

        if (!camera.getUserId().equals(user.getUserId())) {
            throw new RuntimeException("해당 카메라에 대한 접근 권한이 없습니다");
        }

        String streamUrl = "rtsp://stream.example.com/camera/" + cameraId;

        return Map.of(
                "streamUrl", streamUrl,
                "cameraId", cameraId.toString(),
                "cameraName", camera.getCameraName()
        );
    }


    @Transactional
    public boolean controlCamera(Integer cameraId, String email, String direction) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + email));

        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new IllegalArgumentException("카메라를 찾을 수 없습니다: " + cameraId));

        if (!camera.getUserId().equals(user.getUserId())) {
            throw new RuntimeException("해당 카메라에 대한 접근 권한이 없습니다");
        }

        if (!isValidDirection(direction)) {
            throw new IllegalArgumentException("유효하지 않은 방향입니다: " + direction);
        }

        return true;
    }


    private boolean isValidDirection(String direction) {
        return direction != null &&
                (direction.equals("up") ||
                        direction.equals("down") ||
                        direction.equals("left") ||
                        direction.equals("right"));
    }


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
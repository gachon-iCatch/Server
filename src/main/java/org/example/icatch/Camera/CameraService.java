package org.example.icatch.Camera;

import org.example.icatch.Device.Device;
import org.example.icatch.Device.DeviceRepository;
import org.example.icatch.User.User;
import org.example.icatch.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.icatch.Notification.NotificationRepository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CameraService {

    private final CameraRepository cameraRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;


    @Autowired
    public CameraService(CameraRepository cameraRepository,
                         UserRepository userRepository,
                        NotificationRepository notificationRepository) {
        this.cameraRepository = cameraRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Camera setDangerZone(Integer cameraId, Integer userId, List<Integer> zoneNumbers) {
        Optional<Camera> optionalCamera = cameraRepository.findById(cameraId);
        if (!optionalCamera.isPresent()) {
            throw new RuntimeException("해당 카메라를 찾을 수 없습니다: " + cameraId);
        }

        Camera camera = optionalCamera.get();

        // userId가 null이 아닌 경우에만 사용자 권한 체크
        if (userId != null && !camera.getUserId().equals(userId)) {
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

        // userId가 null이 아닌 경우에만 사용자 권한 체크
        if (userId != null && !camera.getUserId().equals(userId)) {
            throw new RuntimeException("해당 카메라에 대한 접근 권한이 없습니다");
        }

        return camera.getDangerZone();
    }

    @Autowired
    private DeviceRepository deviceRepository; // 추가 필요

    @Transactional
    public Integer setupCamera(Integer userId, Integer deviceId, Integer targetId, String cameraName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));

        Device device = deviceRepository.findById(Long.valueOf(deviceId))
                .orElseThrow(() -> new IllegalArgumentException("디바이스를 찾을 수 없습니다: " + deviceId));

        Camera camera = new Camera();
        camera.setUser(user);
        camera.setDevice(device);
        camera.setTargetId(targetId);
        camera.setCameraName(cameraName);
        camera.setIsEnabled("yes");
        camera.setMotionDetectionEnabled(true);
        camera.setCreatedAt(LocalDateTime.now());

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

        List<Camera> cameras = cameraRepository.findByUser_UserId(user.getUserId());

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

        // Device에서 IP 가져오기
        String deviceIp = "";
        if (camera.getDevice() != null) {
            deviceIp = camera.getDevice().getDeviceIp();
        }

        // IP 암호화
        String encryptedIp = encryptIpAddress(deviceIp);

        String streamUrl = "rtsp://stream.example.com/camera/" + cameraId;

        return Map.of(
                "streamUrl", streamUrl,
                "cameraId", cameraId.toString(),
                "cameraName", camera.getCameraName(),
                "deviceIp", encryptedIp  // 암호화된 IP 추가
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


    @Transactional
    public boolean deleteCamera(Integer cameraId, Integer userId) {
        // 카메라 소유권 확인
        Camera camera = cameraRepository.findById(cameraId)
                .orElse(null);

        if (camera == null || !camera.getUser().getUserId().equals(userId)) {
            return false;
        }

        // 카메라와 관련된 알림 먼저 삭제
        notificationRepository.deleteByCameraId(cameraId);

        // 카메라 삭제
        cameraRepository.delete(camera);
        return true;
    }



    @Transactional(readOnly = true)
    public int countCamerasByUserId(Integer userId) {
        return cameraRepository.countByUser_UserId(userId);
    }

    // 카메라 연결 상태 확인 메서드 (실제 구현은 시스템에 맞게 조정 필요)
    public boolean checkCameraConnection(Integer cameraId) {
        Optional<Camera> optionalCamera = cameraRepository.findById(cameraId);
        if (!optionalCamera.isPresent()) {
            return false;
        }

        Camera camera = optionalCamera.get();

        // 실제 카메라와의 연결 상태를 확인하는 로직 구현
        // 예: RTSP 스트림 연결 상태 확인 등

        return true; // 임시 구현: 항상 연결됨으로 가정
    }

// 카메라 IP 주소 다 보이도록 수정
    public String encryptIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return "";
        }

        // 원본 IP 주소 그대로 반환
        return ipAddress;
    }

    @Transactional(readOnly = true)
    public Camera getCameraById(Integer cameraId) {
        return cameraRepository.findById(cameraId)
                .orElseThrow(() -> new IllegalArgumentException("카메라를 찾을 수 없습니다: " + cameraId));
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

        // Device IP 추가 및 암호화
        String deviceIp = "";
        if (camera.getDevice() != null) {
            deviceIp = camera.getDevice().getDeviceIp();
        }
        dto.setDeviceIp(encryptIpAddress(deviceIp)); // 암호화된 IP 설정

        return dto;
    }
}
package org.example.icatch.service;

import org.example.icatch.dto.DeviceAuthRequest;
import org.example.icatch.dto.DeviceAuthResponse;
import org.example.icatch.model.Device;
import org.example.icatch.model.User;
import org.example.icatch.repository.CameraRepository;
import org.example.icatch.repository.DeviceRepository;
import org.example.icatch.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final CameraRepository cameraRepository;

    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository, CameraRepository cameraRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.cameraRepository = cameraRepository;

    }

    public DeviceAuthResponse registerDevice(DeviceAuthRequest deviceAuthRequest) {
        User user = userRepository.findById(deviceAuthRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Device device = Device.builder()
                .device_ip(deviceAuthRequest.getDeviceIp())
                .user_id(user)
                .build();

        Camera camera = Camera.builder()
                .user(user)
                .device(device)
                .build();

        deviceRepository.save(device);
        cameraRepository.save(camera);

        return DeviceAuthResponse.builder()
                .deviceId(device.getDevice_id())
                .cameraId(camera.getCamera_id())
                .userId(user.getId())
                .build();
    }
}

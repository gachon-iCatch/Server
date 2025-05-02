package org.example.icatch.Device;

import org.example.icatch.Camera.Camera;
import org.example.icatch.User.User;
import org.example.icatch.Camera.CameraRepository;
import org.example.icatch.User.UserRepository;
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
                .deviceIp(deviceAuthRequest.getDeviceIp())
                .user(user)
                .build();

        Camera camera = Camera.builder()
                .userId(user)
                .device(device)
                .build();

        deviceRepository.save(device);
        cameraRepository.save(camera);

        return DeviceAuthResponse.builder()
                .deviceId(device.getDeviceId())
                .cameraId(camera.getCameraId())
                .userId(user.getUserId())
                .build();
    }
}

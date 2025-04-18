package org.example.icatch.service;

import org.example.icatch.dto.DeviceAuthRequest;
import org.example.icatch.dto.DeviceAuthResponse;
import org.example.icatch.model.Camera;
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

        Device device = new Device();
        device.setDeviceIp(deviceAuthRequest.getDeviceIp());
        device.setUser(user);

        Camera camera = new Camera();
        camera.setUser(user);
        camera.setDevice(device);

        deviceRepository.save(device);
        cameraRepository.save(camera);

        return new DeviceAuthResponse(device.getDeviceId(), camera.getCameraId(), user.getUserId());
    }


}

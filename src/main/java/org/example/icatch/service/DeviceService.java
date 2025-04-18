package org.example.icatch.service;

import org.example.icatch.dto.DeviceAuthRequest;
import org.example.icatch.model.Device;
import org.example.icatch.model.User;
import org.example.icatch.repository.DeviceRepository;
import org.example.icatch.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    public Device registerDevice(DeviceAuthRequest deviceAuthRequest) {
        User user = userRepository.findById(deviceAuthRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Device device = Device.builder()
                .deviceIp(deviceAuthRequest.getDeviceIp())
                .user(user)
                .build();
        return deviceRepository.save(device);
    }
}

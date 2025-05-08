package org.example.icatch.Device;


import org.example.icatch.Camera.Camera;
import org.example.icatch.Camera.CameraRepository;
import org.example.icatch.User.User;
import org.example.icatch.User.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.util.Optional;

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
        Long userIdLong = deviceAuthRequest.getUserId();
        Integer userId = userIdLong.intValue();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Device device = new Device();
        device.setDeviceIp(deviceAuthRequest.getDeviceIp());
        device.setUser(user);

        Camera camera = new Camera();
        camera.setUser(user);
        camera.setDevice(device);

        deviceRepository.save(device);
        cameraRepository.save(camera);

        return new DeviceAuthResponse(device.getDeviceId(), device.getDeviceIp(), camera.getCameraId());
    }

    public DeviceAuthResponse findDevice(Integer userId) {
        Optional<Device> OptionalDevice = deviceRepository.findFirstByUser_UserIdOrderByCreatedAtDesc(userId);
        Device device = OptionalDevice.get();
        Optional<Camera> OptionalCamera = cameraRepository.findFirstByUser_UserIdOrderByCreatedAtDesc(userId);
        Camera camera = OptionalCamera.get();
        return new DeviceAuthResponse(device.getDeviceId(),device.getDeviceIp(),camera.getCameraId());
    }

    public Resource updateModel() throws FileNotFoundException{
        String modelPath = "/home/t25104/Server/src/main/resources/static/model/best_0414.pt"; // 예시 경로
        File file = new File(modelPath);
        if (!file.exists()){
            return null;
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return resource;
    }
}
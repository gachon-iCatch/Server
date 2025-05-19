package org.example.icatch.Device;


import org.example.icatch.Camera.Camera;
import org.example.icatch.Camera.CameraRepository;
import org.example.icatch.User.User;
import org.example.icatch.User.UserRepository;
import org.example.icatch.Picture.Picture;
import org.example.icatch.Picture.PictureRepository;
import org.springframework.stereotype.Service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;
    private final CameraRepository cameraRepository;
    private final PictureRepository pictureRepository;

    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository, CameraRepository cameraRepository, PictureRepository pictureRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
        this.cameraRepository = cameraRepository;
        this.pictureRepository = pictureRepository;
    }
    
    @Transactional
    public DeviceAuthResponse registerDevice(DeviceAuthRequest deviceAuthRequest) {
        Integer userId = deviceAuthRequest.getUserId().intValue();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Device device = new Device();
        device.setDeviceIp(deviceAuthRequest.getDeviceIp());
        device.setUser(user);
        device.setDeviceStatus(deviceAuthRequest.getDeviceStatus());
        device = deviceRepository.save(device);
        System.out.println(device.getDeviceId());
        Camera camera = new Camera();
        
        camera.setUser(user);
        camera.setDevice(device);
        camera.setCreatedAt(LocalDateTime.now());
        camera = cameraRepository.save(camera);

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

    public String uploadImage(ImageRequest imageRequest, MultipartFile file) throws IOException { 
            Date timestamp = imageRequest.getTimestamp();
            LocalDateTime date = timestamp.toInstant()
                                  .atZone(ZoneId.systemDefault())
                                  .toLocalDateTime();
            SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
            SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd");

            String year = yearFormat.format(timestamp);
            String month = monthFormat.format(timestamp);
            String day = dayFormat.format(timestamp);

            // 사용자 ID와 디바이스 ID
            Integer userId = imageRequest.getUserId();
            long deviceId = imageRequest.getDeviceId();
            
            String timestampStr = String.valueOf(timestamp);
            String originalContentType = file.getContentType(); // 예: image/jpeg, image/png
            String extension;
             if ("image/jpeg".equals(originalContentType)) {
                extension = ".jpg";
            } else if ("image/png".equals(originalContentType)) {
                extension = ".png";
            } else {
                return "지원하지 않는 이미지 형식입니다.";
            }

            String newFileName = deviceId + "_" + timestampStr + extension;

           
            String uploadDir = "/home/t25104/Server/image/" + year + "/" + month + "/" + day + "/" + userId + "/";

            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String destPath = uploadDir + newFileName;
            File dest = new File(destPath);
            file.transferTo(dest);

            Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("device not found"));

            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

            Picture picture = new Picture();
            picture.setDevice(device);
            picture.setImagePath(destPath);
            picture.setUser(user);
            picture.setCaptureTime(date);
            picture = pictureRepository.save(picture);
            return destPath;
    }

    public String updateDevice(DeviceAuthRequest deviceAuthRequest, MultipartFile file) throws IOException{
        Integer deviceId = deviceAuthRequest.getDeviceId();
        Date date = new Date();
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        String day = dayFormat.format(date);
        String uploadDir = "/home/t25104/Server/log/" + deviceId +"/" ;
        File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
        }
        String newFilename = day + ".zip";
        File dest = new File(uploadDir, newFilename);
        file.transferTo(dest);
        return uploadDir+newFilename;
    }

    public List<DeviceMonitoringResponse> monitoring(){
        List<Device> devices = deviceRepository.findAll();
        List<DeviceMonitoringResponse> deviceMonitoringResponse = devices.stream()
                .map(device -> {
                    User user = device.getUser();
                    List<Camera> cameras = cameraRepository.findByUser(user);
                    Camera camera = cameras.isEmpty() ? null : cameras.get(0);
                    return new DeviceMonitoringResponse(
                            device.getDeviceId(),
                            camera != null ? camera.getCameraName() : "Unknown", // 카메라 이름 가져오기
                            user.getUserNickname(),
                            device.getDeviceIp(),
                            device.getDeviceStatus()
                    );
                })
                .collect(Collectors.toList());
        
        return deviceMonitoringResponse; // 리스트 반환
    }

    public String status(DeviceAuthRequest deviceAuthRequest){
         Integer deviceId = deviceAuthRequest.getDeviceId().intValue();
         Device device = deviceRepository.findById(deviceId.longValue())
                .orElseThrow(() -> new RuntimeException("Device not found"));
         device.setDeviceStatus(deviceAuthRequest.getDeviceStatus());
         deviceRepository.save(device);
         return deviceAuthRequest.getDeviceStatus().toString();
    }

}

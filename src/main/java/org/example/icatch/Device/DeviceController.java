package org.example.icatch.Device;


import org.example.icatch.security.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/device")
public class DeviceController {
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/auth/authenticate")
    public ResponseEntity<ApiResponse> authenticate(@RequestBody DeviceAuthRequest deviceAuthRequest) {
        try{
            DeviceAuthResponse data = deviceService.registerDevice(deviceAuthRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("디바이스 등록이 완료되었습니다", data));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    @GetMapping("/auth/authenticate")
    public ResponseEntity<ApiResponse> getAuthenticate(@RequestParam Integer userId) {
        try{

            DeviceAuthResponse data = deviceService.findDevice(userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("데이터 전송에 성공하였습니다", data));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    @GetMapping("/model/update")
    public ResponseEntity<?> getAuthenticate() {
        try{
            Resource resource = deviceService.updateModel();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"model.pt\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}

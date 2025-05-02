package org.example.icatch.Device;


import org.example.icatch.security.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/device")
public class DeviceController {
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/auth/authenticate")
    public ResponseEntity<ApiResponse> authenticate(@RequestBody DeviceAuthRequest deviceAuthRequest) {
        try {
            DeviceAuthResponse data = deviceService.registerDevice(deviceAuthRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("디바이스 등록이 완료되었습니다", data));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}

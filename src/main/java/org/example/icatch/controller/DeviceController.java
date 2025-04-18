package org.example.icatch.controller;


import org.example.icatch.dto.ApiResponse;
import org.example.icatch.dto.DeviceAuthRequest;
import org.example.icatch.dto.DeviceAuthResponse;
import org.example.icatch.model.Device;
import org.example.icatch.service.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/device")
public class DeviceController {
    private final DeviceService deviceService;
    public DeviceController(DeviceService deviceService) {this.deviceService = deviceService;}

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
}

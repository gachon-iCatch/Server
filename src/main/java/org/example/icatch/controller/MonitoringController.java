package org.example.icatch.controller;

import org.example.icatch.dto.ApiResponse;
import org.example.icatch.dto.TargetCreateRequest;
import org.example.icatch.service.CameraService;
import org.example.icatch.service.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

    private final TargetService targetService;
    private final CameraService cameraService;

    @Autowired
    public MonitoringController(TargetService targetService, CameraService cameraService) {
        this.targetService = targetService;
        this.cameraService = cameraService;
    }

    @PostMapping("/targets/create")
    public ResponseEntity<ApiResponse> createTarget(@RequestBody TargetCreateRequest request) {
        try {
            // 1. 타겟 객체 생성
            Integer targetId = targetService.createTarget(request);

            // 2. 생성된 타겟 ID와 함께 카메라 이름 설정
            cameraService.setupCamera(request.getUser_Id(), request.getDevice_Id(), targetId, request.getCamera_name());

            // 기존 ApiResponse 클래스의 static 메소드 사용
            return ResponseEntity.ok(ApiResponse.success("Target and camera created successfully", targetId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }
}
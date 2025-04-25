package org.example.icatch.controller;

import org.example.icatch.dto.TargetCreateRequest;
import org.example.icatch.service.TargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring/targets")
public class TargetController {

    private final TargetService targetService;

    @Autowired
    public TargetController(TargetService targetService) {
        this.targetService = targetService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTarget(@RequestBody TargetCreateRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // userId 체크 (필수값)
            if (request.getUser_Id() == null) {
                response.put("success", false);
                response.put("message", "User ID is required");
                return ResponseEntity.badRequest().body(response);
            }

            // target_Type 체크 (필수값)
            if (request.getTarget_Type() == null || request.getTarget_Type().isEmpty()) {
                response.put("success", false);
                response.put("message", "Target type is required");
                return ResponseEntity.badRequest().body(response);
            }

            // target_Type 값 검증
            try {
                org.example.icatch.model.Target.TargetType.valueOf(request.getTarget_Type().toLowerCase());
            } catch (IllegalArgumentException e) {
                response.put("success", false);
                response.put("message", "Invalid target type. Available types: person, pet");
                return ResponseEntity.badRequest().body(response);
            }

            // 타겟 생성
            Integer targetId = targetService.createTarget(request);

            response.put("success", true);
            response.put("targetId", targetId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
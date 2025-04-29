package org.example.icatch.controller;

import org.example.icatch.dto.CameraDto;
import org.example.icatch.model.Camera;
import org.example.icatch.service.CameraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cameras")
public class CameraController {

    private final CameraService cameraService;

    @Autowired
    public CameraController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

    @PostMapping("/setup")
    public ResponseEntity<Map<String, Integer>> setupCamera(

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("cameraId", cameraId));
    }

    @PostMapping("/{cameraId}/danger-zone")
    public ResponseEntity<Camera> setDangerZone(
            @PathVariable Integer cameraId,
            @RequestParam Integer userId,
            @RequestBody List<Integer> zoneNumbers) {

        Camera camera = cameraService.setDangerZone(cameraId, userId, zoneNumbers);
        return ResponseEntity.ok(camera);
    }

    @GetMapping("/{cameraId}/danger-zone")
    public ResponseEntity<Map<String, String>> getDangerZone(
            @PathVariable Integer cameraId,
            @RequestParam Integer userId) {

        String dangerZone = cameraService.getDangerZone(cameraId, userId);
        return ResponseEntity.ok(Map.of("dangerZone", dangerZone));
    }
}
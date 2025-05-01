package org.example.icatch.Camera;

import org.example.icatch.security.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

    @GetMapping("/{cameraId}")
    public ResponseEntity<ApiResponse> getMonitoringStream(
            @PathVariable Integer cameraId,
            @RequestParam Integer userId) {
        try {

            return ResponseEntity.ok(ApiResponse.success("Monitoring stream information",
                    Map.of("streamUrl", "rtsp://example.com/stream/" + cameraId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{cameraId}/status")
    public ResponseEntity<ApiResponse> getMonitoringStatus(
            @PathVariable Integer cameraId,
            @RequestParam Integer userId) {
        try {
            return ResponseEntity.ok(ApiResponse.success("Monitoring status",
                    Map.of("status", "active", "targetDetected", true)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
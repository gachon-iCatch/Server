package org.example.icatch.Target;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            if (request.getUserId() == null) {
                response.put("success", false);
                response.put("message", "User ID is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (request.getTargetType() == null || request.getTargetType().isEmpty()) {
                response.put("success", false);
                response.put("message", "Target type is required");
                return ResponseEntity.badRequest().body(response);
            }

            try {
                Target.TargetType.valueOf(request.getTargetType().toLowerCase());
            } catch (IllegalArgumentException e) {
                response.put("success", false);
                response.put("message", "Invalid target type. Available types: person, pet");
                return ResponseEntity.badRequest().body(response);
            }

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
    //목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getTargetsByUserId(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Target> targets = targetService.getTargetsByUserId(userId);
            List<TargetResponseDTO> targetDTOs = targets.stream()
                    .map(TargetResponseDTO::new)
                    .collect(Collectors.toList());

            response.put("success", true);
            response.put("targets", targetDTOs);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 삭제
    @DeleteMapping("/{targetId}")
    public ResponseEntity<?> deleteTarget(@PathVariable Integer targetId) {
        Map<String, Object> response = new HashMap<>();

        try {
            targetService.deleteTarget(targetId);

            response.put("success", true);
            response.put("message", "타겟이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/{targetId}")
    public ResponseEntity<?> updateTarget(@PathVariable Integer targetId, @RequestBody TargetUpdateRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            targetService.updateTarget(targetId, request);

            response.put("success", true);
            response.put("message", "타겟이 성공적으로 업데이트되었습니다.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
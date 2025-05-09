package org.example.icatch.Gesture;

import org.example.icatch.security.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/gestures")
public class GestureController {

    private final GestureService gestureService;
    private final GestureActionRepository gestureActionRepository;

    @Autowired
    public GestureController(GestureService gestureService, GestureActionRepository gestureActionRepository) {  // 수정
        this.gestureService = gestureService;
        this.gestureActionRepository = gestureActionRepository;
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserGestures(@PathVariable Integer userId) {
        List<Gesture> gestures = gestureService.getGesturesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved user gestures", gestures));
    }

    @GetMapping("/camera/{cameraId}")
    public ResponseEntity<ApiResponse> getCameraGestures(@PathVariable Integer cameraId) {
        List<Gesture> gestures = gestureService.getGesturesByCameraId(cameraId);
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved camera gestures", gestures));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createGesture(@RequestBody Gesture gesture) {
        Gesture createdGesture = gestureService.createGesture(gesture);
        return ResponseEntity.ok(ApiResponse.success("Successfully created gesture", createdGesture));
    }

    @PutMapping("/{gestureId}")
    public ResponseEntity<ApiResponse> updateGesture(
            @PathVariable Integer gestureId,
            @RequestBody Gesture gesture) {
        gesture.setGestureId(gestureId);
        Gesture updatedGesture = gestureService.updateGesture(gesture);
        return ResponseEntity.ok(ApiResponse.success("Successfully updated gesture", updatedGesture));
    }

    @PutMapping("/{gestureId}/action/{actionId}")
    public ResponseEntity<ApiResponse> associateGestureWithAction(
            @PathVariable Integer gestureId,
            @PathVariable Integer actionId) {
        Gesture updatedGesture = gestureService.associateGestureWithAction(gestureId, actionId);
        return ResponseEntity.ok(ApiResponse.success("Successfully associated gesture with action", updatedGesture));
    }

    @PutMapping("/{gestureId}/enable/{isEnabled}")
    public ResponseEntity<ApiResponse> setGestureEnabled(
            @PathVariable Integer gestureId,
            @PathVariable String isEnabled) {
        Gesture updatedGesture = gestureService.setGestureEnabled(gestureId, isEnabled);
        return ResponseEntity.ok(ApiResponse.success("Successfully updated gesture status", updatedGesture));
    }

    @DeleteMapping("/{gestureId}")
    public ResponseEntity<ApiResponse> deleteGesture(@PathVariable Integer gestureId) {
        gestureService.deleteGesture(gestureId);
        return ResponseEntity.ok(ApiResponse.success("Successfully deleted gesture"));
    }

    @PutMapping("/{gestureId}/select-function")
    public ResponseEntity<ApiResponse> selectGestureFunction(
            @PathVariable Integer gestureId,
            @RequestBody Map<String, String> request) {
        String selectedFunction = request.get("selectedFunction");
        try {
            Gesture updatedGesture = gestureService.selectGestureFunction(gestureId, selectedFunction);

            // DTO로 변환하여 반환
            GestureActionDto dto = new GestureActionDto();
            dto.setGestureId(updatedGesture.getGestureId());
            dto.setUserId(updatedGesture.getUserId());
            dto.setCameraId(updatedGesture.getCameraId());
            dto.setGestureName(updatedGesture.getGestureName());
            dto.setGestureType(updatedGesture.getGestureType());
            dto.setGestureDescription(updatedGesture.getGestureDescription());
            dto.setGestureImagePath(updatedGesture.getGestureImagePath());
            dto.setIsEnabled(updatedGesture.getIsEnabled());

            if (updatedGesture.getActionId() != null) {
                GestureAction action = gestureActionRepository.findById(updatedGesture.getActionId()).orElse(null);
                if (action != null) {
                    dto.setSelectedFunction(action.getSelectedFunction());
                    dto.setMessage(action.getMessage());
                }
            }

            return ResponseEntity.ok(ApiResponse.success("Successfully selected function for gesture", dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/{gestureId}")
    public ResponseEntity<ApiResponse> getGestureById(@PathVariable Integer gestureId) {
        try {
            Gesture gesture = gestureService.getGestureById(gestureId);
            return ResponseEntity.ok(ApiResponse.success("Successfully retrieved gesture", gesture));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/user/{userId}/enable/{isEnabled}")
    public ResponseEntity<ApiResponse> setAllUserGesturesEnabled(
            @PathVariable Integer userId,
            @PathVariable String isEnabled) {

        if (!isEnabled.equals("yes") && !isEnabled.equals("no")) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Invalid value for isEnabled. Must be 'yes' or 'no'."));
        }

        gestureService.setGesturesEnabledByUser(userId, isEnabled);
        return ResponseEntity.ok(ApiResponse.success("Successfully updated gestures status"));
    }
    @GetMapping("/user/{userId}/with-actions")
    public ResponseEntity<ApiResponse> getUserGesturesWithActions(@PathVariable Integer userId) {
        List<GestureActionDto> gestures = gestureService.getGesturesWithActionsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved user gestures with actions", gestures));
    }
}
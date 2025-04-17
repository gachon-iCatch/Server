package org.example.icatch.controller;

import org.example.icatch.dto.ApiResponse;
import org.example.icatch.model.Gesture;
import org.example.icatch.service.GestureService;
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

    @Autowired
    public GestureController(GestureService gestureService) {
        this.gestureService = gestureService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllGestures() {
        List<Gesture> gestures = gestureService.getAllGestures();
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved all gestures", gestures));
    }

    @GetMapping("/{userId}")
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
            return ResponseEntity.ok(ApiResponse.success("Successfully selected function for gesture", updatedGesture));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
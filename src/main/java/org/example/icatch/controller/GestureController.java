package org.example.icatch.controller;

import org.example.icatch.dto.ApiResponse;
import org.example.icatch.dto.GestureActionDto;
import org.example.icatch.model.Gesture;
import org.example.icatch.model.GestureAction;
import org.example.icatch.service.GestureActionService;
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
    private final GestureActionService gestureActionService;

    @Autowired
    public GestureController(GestureService gestureService, GestureActionService gestureActionService) {
        this.gestureService = gestureService;
        this.gestureActionService = gestureActionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllGestures() {
        List<Gesture> gestures = gestureService.getAllGestures();
        return ResponseEntity.ok(ApiResponse.success("모든 제스처를 성공적으로 조회했습니다", gestures));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserGestures(@PathVariable Integer userId) {
        List<Gesture> gestures = gestureService.getGesturesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("사용자 제스처를 성공적으로 조회했습니다", gestures));
    }

    @GetMapping("/camera/{cameraId}")
    public ResponseEntity<ApiResponse> getCameraGestures(@PathVariable Integer cameraId) {
        List<Gesture> gestures = gestureService.getGesturesByCameraId(cameraId);
        return ResponseEntity.ok(ApiResponse.success("카메라 제스처를 성공적으로 조회했습니다", gestures));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createGesture(@RequestBody Gesture gesture) {
        Gesture createdGesture = gestureService.createGesture(gesture);
        return ResponseEntity.ok(ApiResponse.success("제스처가 성공적으로 생성되었습니다", createdGesture));
    }

    @PutMapping("/{gestureId}")
    public ResponseEntity<ApiResponse> updateGesture(
            @PathVariable Integer gestureId,
            @RequestBody Gesture gesture) {
        gesture.setGestureId(gestureId);
        Gesture updatedGesture = gestureService.updateGesture(gesture);
        return ResponseEntity.ok(ApiResponse.success("제스처가 성공적으로 업데이트되었습니다", updatedGesture));
    }

    @DeleteMapping("/{gestureId}")
    public ResponseEntity<ApiResponse> deleteGesture(@PathVariable Integer gestureId) {
        gestureService.deleteGesture(gestureId);
        return ResponseEntity.ok(ApiResponse.success("제스처가 성공적으로 삭제되었습니다"));
    }

    @GetMapping("/actions")
    public ResponseEntity<ApiResponse> getAllGestureActions() {
        List<GestureAction> actions = gestureActionService.getAllGestureActions();
        return ResponseEntity.ok(ApiResponse.success("모든 제스처 액션을 성공적으로 조회했습니다", actions));
    }

    @GetMapping("/actions/{actionId}")
    public ResponseEntity<ApiResponse> getGestureActionById(@PathVariable Integer actionId) {
        GestureAction action = gestureActionService.getGestureActionById(actionId);
        return ResponseEntity.ok(ApiResponse.success("제스처 액션을 성공적으로 조회했습니다", action));
    }

    @PostMapping("/actions")
    public ResponseEntity<ApiResponse> createGestureAction(@RequestBody GestureAction gestureAction) {
        GestureAction createdAction = gestureActionService.createGestureAction(gestureAction);
        return ResponseEntity.ok(ApiResponse.success("제스처 액션이 성공적으로 생성되었습니다", createdAction));
    }

    @PutMapping("/actions/{actionId}")
    public ResponseEntity<ApiResponse> updateGestureAction(
            @PathVariable Integer actionId,
            @RequestBody GestureAction gestureAction) {
        gestureAction.setActionId(actionId);
        GestureAction updatedAction = gestureActionService.updateGestureAction(gestureAction);
        return ResponseEntity.ok(ApiResponse.success("제스처 액션이 성공적으로 업데이트되었습니다", updatedAction));
    }

    @DeleteMapping("/actions/{actionId}")
    public ResponseEntity<ApiResponse> deleteGestureAction(@PathVariable Integer actionId) {
        gestureActionService.deleteGestureAction(actionId);
        return ResponseEntity.ok(ApiResponse.success("제스처 액션이 성공적으로 삭제되었습니다"));
    }

    @PutMapping("/{gestureId}/link-action/{actionId}")
    public ResponseEntity<ApiResponse> associateGestureWithAction(
            @PathVariable Integer gestureId,
            @PathVariable Integer actionId) {
        Gesture updatedGesture = gestureService.associateGestureWithAction(gestureId, actionId);
        return ResponseEntity.ok(ApiResponse.success("제스처와 액션이 성공적으로 연결되었습니다", updatedGesture));
    }

    @PostMapping("/complete-setup")
    public ResponseEntity<ApiResponse> completeGestureSetup(@RequestBody GestureActionDto dto) {
        Gesture gesture = gestureService.createGestureWithAction(dto);
        return ResponseEntity.ok(ApiResponse.success("제스처 설정이 완료되었습니다", gesture));
    }

    @GetMapping("/{gestureId}/with-action")
    public ResponseEntity<ApiResponse> getGestureWithAction(@PathVariable Integer gestureId) {
        GestureActionDto dto = gestureService.getGestureWithAction(gestureId);
        return ResponseEntity.ok(ApiResponse.success("제스처와 액션 정보 조회 성공", dto));
    }

    @GetMapping("/user/{userId}/with-actions")
    public ResponseEntity<ApiResponse> getUserGesturesWithActions(@PathVariable Integer userId) {
        List<GestureActionDto> gestures = gestureService.getGesturesWithActionsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("사용자의 제스처 및 액션 정보 조회 성공", gestures));
    }

    @PostMapping("/setup")
    public ResponseEntity<ApiResponse> setupGestureWithFunction(@RequestBody GestureActionDto dto) {
        Gesture gesture = gestureService.createGestureWithAction(dto);
        return ResponseEntity.ok(ApiResponse.success("제스처 이미지 및 기능 설정이 완료되었습니다", gesture));
    }

    @PutMapping("/{gestureId}/setup")
    public ResponseEntity<ApiResponse> updateGestureSetup(
            @PathVariable Integer gestureId,
            @RequestBody GestureActionDto dto) {
        Gesture updatedGesture = gestureService.updateGestureWithAction(gestureId, dto);
        return ResponseEntity.ok(ApiResponse.success("제스처 이미지 및 기능 설정이 업데이트되었습니다", updatedGesture));
    }

    @PutMapping("/{gestureId}/function")
    public ResponseEntity<ApiResponse> setSelectedFunction(
            @PathVariable Integer gestureId,
            @RequestBody Map<String, String> request) {
        String functionName = request.get("selectedFunction");
        try {
            Gesture updatedGesture = gestureService.selectGestureFunction(gestureId, functionName);
            return ResponseEntity.ok(ApiResponse.success("제스처 기능이 성공적으로 업데이트되었습니다", updatedGesture));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("잘못된 기능: " + functionName));
        }
    }

    @PutMapping("/{gestureId}/message")
    public ResponseEntity<ApiResponse> setGestureMessage(
            @PathVariable Integer gestureId,
            @RequestBody Map<String, String> request) {
        String message = request.get("message");
        Gesture gesture = gestureService.findById(gestureId);
        GestureAction updatedAction = gestureActionService.setMessage(gesture.getActionId(), message);
        return ResponseEntity.ok(ApiResponse.success("제스처 메시지가 성공적으로 업데이트되었습니다", updatedAction));
    }
}
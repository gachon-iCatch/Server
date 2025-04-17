package org.example.icatch.controller;

import org.example.icatch.dto.ApiResponse;
import org.example.icatch.model.GestureAction;
import org.example.icatch.service.GestureActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gesture-actions")
public class GestureActionController {

    private final GestureActionService gestureActionService;

    @Autowired
    public GestureActionController(GestureActionService gestureActionService) {
        this.gestureActionService = gestureActionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getAllGestureActions() {
        List<GestureAction> actions = gestureActionService.getAllGestureActions();
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved all gesture actions", actions));
    }

    @GetMapping("/{actionId}")
    public ResponseEntity<ApiResponse> getGestureActionById(@PathVariable Integer actionId) {
        GestureAction action = gestureActionService.getGestureActionById(actionId);
        return ResponseEntity.ok(ApiResponse.success("Successfully retrieved gesture action", action));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createGestureAction(@RequestBody GestureAction gestureAction) {
        GestureAction createdAction = gestureActionService.createGestureAction(gestureAction);
        return ResponseEntity.ok(ApiResponse.success("Successfully created gesture action", createdAction));
    }

    @PutMapping("/{actionId}")
    public ResponseEntity<ApiResponse> updateGestureAction(
            @PathVariable Integer actionId,
            @RequestBody GestureAction gestureAction) {
        gestureAction.setActionId(actionId);
        GestureAction updatedAction = gestureActionService.updateGestureAction(gestureAction);
        return ResponseEntity.ok(ApiResponse.success("Successfully updated gesture action", updatedAction));
    }

    @DeleteMapping("/{actionId}")
    public ResponseEntity<ApiResponse> deleteGestureAction(@PathVariable Integer actionId) {
        gestureActionService.deleteGestureAction(actionId);
        return ResponseEntity.ok(ApiResponse.success("Successfully deleted gesture action"));
    }

    @PutMapping("/{actionId}/function")
    public ResponseEntity<ApiResponse> setSelectedFunction(
            @PathVariable Integer actionId,
            @RequestBody Map<String, String> request) {
        String functionName = request.get("selectedFunction");
        try {
            GestureAction.SelectedFunction function = GestureAction.SelectedFunction.valueOf(functionName);
            GestureAction updatedAction = gestureActionService.setSelectedFunction(actionId, function);
            return ResponseEntity.ok(ApiResponse.success("Successfully updated function", updatedAction));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid function: " + functionName));
        }
    }

    @PutMapping("/{actionId}/message")
    public ResponseEntity<ApiResponse> setMessage(
            @PathVariable Integer actionId,
            @RequestBody String message) {
        GestureAction updatedAction = gestureActionService.setMessage(actionId, message);
        return ResponseEntity.ok(ApiResponse.success("Successfully updated message", updatedAction));
    }
}
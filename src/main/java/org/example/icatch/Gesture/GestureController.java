package org.example.icatch.Gesture;

import org.example.icatch.security.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/gestures")
public class GestureController {

    private final GestureService gestureService;
    private final GestureActionRepository gestureActionRepository;

    @Autowired
    public GestureController(GestureService gestureService, GestureActionRepository gestureActionRepository) {
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
    public ResponseEntity<ApiResponse> createGesture(@RequestBody Map<String, Object> requestBody) {
        try {
            // Gesture 객체 생성
            Gesture gesture = new Gesture();

            // 요청 데이터에서 필드 설정
            if (requestBody.containsKey("userId")) {
                gesture.setUserId(Integer.valueOf(requestBody.get("userId").toString()));
            }
            if (requestBody.containsKey("cameraId")) {
                gesture.setCameraId(Integer.valueOf(requestBody.get("cameraId").toString()));
            }
            if (requestBody.containsKey("gestureName")) {
                gesture.setGestureName((String) requestBody.get("gestureName"));
            }
            if (requestBody.containsKey("gestureType")) {
                gesture.setGestureType((String) requestBody.get("gestureType"));
            }
            if (requestBody.containsKey("gestureDescription")) {
                gesture.setGestureDescription((String) requestBody.get("gestureDescription"));
            }
            if (requestBody.containsKey("gestureImagePath")) {
                gesture.setGestureImagePath((String) requestBody.get("gestureImagePath"));
            }
            if (requestBody.containsKey("isEnabled")) {
                gesture.setIsEnabled((String) requestBody.get("isEnabled"));
            }

            // 액션 생성 및 설정
            GestureAction action = new GestureAction();

            // selectedFunction 설정
            if (requestBody.containsKey("selectedFunction") && requestBody.get("selectedFunction") != null) {
                String selectedFunctionStr = (String) requestBody.get("selectedFunction");

                // 기능명 매핑 처리 (클라이언트에서 보내는 값 -> enum 값)
                try {
                    // 대소문자 무시하고 매핑
                    GestureAction.SelectedFunction selectedFunction = null;

                    for (GestureAction.SelectedFunction func : GestureAction.SelectedFunction.values()) {
                        if (func.name().equalsIgnoreCase(selectedFunctionStr)) {
                            selectedFunction = func;
                            break;
                        }
                    }

                    // 매핑 실패 시 특수 처리
                    if (selectedFunction == null) {
                        // 클라이언트에서 보내는 값에 따른 특수 매핑
                        if (selectedFunctionStr.equalsIgnoreCase("OK") ||
                                selectedFunctionStr.contains("괜찮아")) {
                            selectedFunction = GestureAction.SelectedFunction.OK;
                        } else if (selectedFunctionStr.equalsIgnoreCase("HELP") ||
                                selectedFunctionStr.contains("도와줘")) {
                            selectedFunction = GestureAction.SelectedFunction.HELP;
                        } else if (selectedFunctionStr.equalsIgnoreCase("INCONVENIENT") ||
                                selectedFunctionStr.contains("불편해")) {
                            selectedFunction = GestureAction.SelectedFunction.INCONVENIENT;
                        } else if (selectedFunctionStr.equalsIgnoreCase("HELLO") ||
                                selectedFunctionStr.contains("인사")) {
                            selectedFunction = GestureAction.SelectedFunction.HELLO;
                        }
                    }

                    // 선택된 기능 설정 및 적용
                    action.setSelectedFunction(selectedFunction);

                    // 디버깅
                    System.out.println("선택된 기능: " + selectedFunction);
                } catch (Exception e) {
                    System.out.println("기능 매핑 오류: " + e.getMessage());
                    // 오류 발생 시 기본값 설정
                }
            }
            // 메시지 설정
            if (requestBody.containsKey("message") && requestBody.get("message") != null) {
                action.setMessage((String) requestBody.get("message"));
            } else {
                // 선택된 기능에 따라 기본 메시지 설정
                if (action.getSelectedFunction() == GestureAction.SelectedFunction.OK) {
                    action.setMessage("괜찮아~");
                } else if (action.getSelectedFunction() == GestureAction.SelectedFunction.HELP) {
                    action.setMessage("도와줘!");
                } else if (action.getSelectedFunction() == GestureAction.SelectedFunction.INCONVENIENT) {
                    action.setMessage("불편해 ㅠㅠ");
                } else if (action.getSelectedFunction() == GestureAction.SelectedFunction.HELLO) {
                    action.setMessage("인사하기");
                }
            }

            // 액션 저장
            GestureAction savedAction = gestureActionRepository.save(action);

            // 제스처에 액션 연결
            gesture.setActionId(savedAction.getActionId());

            // 제스처 저장
            Gesture createdGesture = gestureService.createGesture(gesture);

            // DTO로 변환하여 반환
            GestureActionDto dto = new GestureActionDto();
            dto.setGestureId(createdGesture.getGestureId());
            dto.setUserId(createdGesture.getUserId());
            dto.setCameraId(createdGesture.getCameraId());
            dto.setGestureName(createdGesture.getGestureName());
            dto.setGestureType(createdGesture.getGestureType());
            dto.setGestureDescription(createdGesture.getGestureDescription());
            dto.setGestureImagePath(createdGesture.getGestureImagePath());
            dto.setIsEnabled(createdGesture.getIsEnabled());
            dto.setSelectedFunction(savedAction.getSelectedFunction());
            dto.setMessage(savedAction.getMessage());

            return ResponseEntity.ok(ApiResponse.success("Successfully created gesture", dto));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(ApiResponse.error("제스처 생성 중 오류 발생: " + e.getMessage()));
        }
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

    @PostMapping("/setup-missing-actions")
    public ResponseEntity<ApiResponse> setupMissingActions() {
        gestureService.setupMissingActions();
        return ResponseEntity.ok(ApiResponse.success("제스처에 누락된 액션을 성공적으로 설정했습니다"));
    }

    @PostMapping("/register_gesture")
    public ResponseEntity<ApiResponse> registerGestureForPython(@RequestBody Map<String, Integer> request) {
        try {
            Integer userId = request.get("userId");
            if (userId == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("사용자 ID가 필요합니다"));
            }

            List<Gesture> userGestures = gestureService.getGesturesByUserId(userId);
            List<Map<String, String>> mappingList = new ArrayList<>();

            // 제스처 ID 매핑 (실제 ID -> 새로운 형식으로 변환)
            Map<Integer, String> gestureIdMapping = new HashMap<>();
            gestureIdMapping.put(0, "Gesture_0");
            gestureIdMapping.put(1, "Gesture_1");
            gestureIdMapping.put(2, "Gesture_2");
            gestureIdMapping.put(3, "Gesture_3");
            gestureIdMapping.put(4, "Gesture_4");
            gestureIdMapping.put(5, "Gesture_5");
            gestureIdMapping.put(6, "Gesture_down");
            gestureIdMapping.put(7, "Gesture_prom");
            gestureIdMapping.put(8, "Gesture_up");

            // 액션 ID 매핑 (기능 이름 -> 새로운 형식으로 변환)
            // 알림 기능 제외
            Map<GestureAction.SelectedFunction, String> actionIdMapping = new HashMap<>();
            actionIdMapping.put(GestureAction.SelectedFunction.BLACK_SCREEN, "black_screen");
            actionIdMapping.put(GestureAction.SelectedFunction.DECLARATION, "declaration");
            actionIdMapping.put(GestureAction.SelectedFunction.PICTURE, "picture");
            actionIdMapping.put(GestureAction.SelectedFunction.OK, "ok");
            actionIdMapping.put(GestureAction.SelectedFunction.HELP, "help");
            actionIdMapping.put(GestureAction.SelectedFunction.INCONVENIENT, "inconvenient");
            actionIdMapping.put(GestureAction.SelectedFunction.HELLO, "hello");

            for (Gesture gesture : userGestures) {
                Map<String, String> gestureInfo = new HashMap<>();

                // 제스처 ID 변환 (실제 ID -> 새로운 형식)
                int gestureIdIndex = gesture.getGestureId() % 9; // 9개의 기본 제스처 ID로 매핑
                gestureInfo.put("gesture_id", gestureIdMapping.getOrDefault(gestureIdIndex, "Gesture_0"));

                // 액션 ID 변환 (액션 ID -> 기능 이름 -> 새로운 형식)
                if (gesture.getActionId() != null) {
                    GestureAction action = gestureActionRepository.findById(gesture.getActionId()).orElse(null);
                    if (action != null && action.getSelectedFunction() != null) {

                        gestureInfo.put("action_id", actionIdMapping.getOrDefault(action.getSelectedFunction(), "ok"));
                    } else {
                        // 기본값
                        gestureInfo.put("action_id", "ok");
                    }
                } else {
                    // 기본값
                    gestureInfo.put("action_id", "ok");
                }

                mappingList.add(gestureInfo);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("gestures", mappingList);

            return ResponseEntity.ok(ApiResponse.success("Python용 제스처-액션 매핑 데이터", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("제스처-액션 매핑 데이터 생성 중 오류 발생: " + e.getMessage()));
        }
    }

    @GetMapping("/export-for-python")
    public ResponseEntity<ApiResponse> exportGestureMappingsForPython() {
        try {
            List<Gesture> allGestures = gestureService.getAllGestures();
            Map<Integer, List<Map<String, String>>> userGestureMap = new HashMap<>();

            // 제스처 ID 매핑 (실제 ID -> 새로운 형식으로 변환)
            Map<Integer, String> gestureIdMapping = new HashMap<>();
            gestureIdMapping.put(0, "Gesture_0");
            gestureIdMapping.put(1, "Gesture_1");
            gestureIdMapping.put(2, "Gesture_2");
            gestureIdMapping.put(3, "Gesture_3");
            gestureIdMapping.put(4, "Gesture_4");
            gestureIdMapping.put(5, "Gesture_5");
            gestureIdMapping.put(6, "Gesture_down");
            gestureIdMapping.put(7, "Gesture_prom");
            gestureIdMapping.put(8, "Gesture_up");

            // 액션 ID 매핑 (기능 이름 -> 새로운 형식으로 변환)
            Map<GestureAction.SelectedFunction, String> actionIdMapping = new HashMap<>();
            actionIdMapping.put(GestureAction.SelectedFunction.BLACK_SCREEN, "black_screen");
            actionIdMapping.put(GestureAction.SelectedFunction.DECLARATION, "declaration");
            actionIdMapping.put(GestureAction.SelectedFunction.PICTURE, "picture");
            actionIdMapping.put(GestureAction.SelectedFunction.OK, "ok");
            actionIdMapping.put(GestureAction.SelectedFunction.HELP, "help");
            actionIdMapping.put(GestureAction.SelectedFunction.INCONVENIENT, "inconvenient");
            actionIdMapping.put(GestureAction.SelectedFunction.HELLO, "hello");
            for (Gesture gesture : allGestures) {
                Integer userId = gesture.getUserId();
                if (!userGestureMap.containsKey(userId)) {
                    userGestureMap.put(userId, new ArrayList<>());
                }

                Map<String, String> gestureInfo = new HashMap<>();

                // 제스처 ID 변환 (실제 ID -> 새로운 형식)
                int gestureIdIndex = gesture.getGestureId() % 9; // 9개의 기본 제스처 ID로 매핑
                gestureInfo.put("gesture_id", gestureIdMapping.getOrDefault(gestureIdIndex, "Gesture_0"));

                // 액션 ID 변환 (액션 ID -> 기능 이름 -> 새로운 형식)
                if (gesture.getActionId() != null) {
                    GestureAction action = gestureActionRepository.findById(gesture.getActionId()).orElse(null);
                    if (action != null && action.getSelectedFunction() != null) {
                        gestureInfo.put("action_id", actionIdMapping.getOrDefault(action.getSelectedFunction(), "ok"));
                    } else {
                        // 기본값
                        gestureInfo.put("action_id", "ok");
                    }
                } else {
                    // 기본값
                    gestureInfo.put("action_id", "ok");
                }

                userGestureMap.get(userId).add(gestureInfo);
            }

            return ResponseEntity.ok(ApiResponse.success("Python용 모든 사용자 제스처 매핑 데이터", userGestureMap));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Python용 데이터 내보내기 중 오류 발생: " + e.getMessage()));
        }
    }
}
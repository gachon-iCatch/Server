package org.example.icatch.Gesture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GestureService {

    private final GestureRepository gestureRepository;
    private final GestureActionRepository gestureActionRepository;

    @Autowired
    public GestureService(GestureRepository gestureRepository, GestureActionRepository gestureActionRepository) {
        this.gestureRepository = gestureRepository;
        this.gestureActionRepository = gestureActionRepository;
    }

    public List<Gesture> getAllGestures() {
        return gestureRepository.findAll();
    }

    public List<Gesture> getGesturesByUserId(Integer userId) {
        return gestureRepository.findByUserId(userId);
    }

    public List<Gesture> getGesturesByCameraId(Integer cameraId) {
        return gestureRepository.findByCameraId(cameraId);
    }

    @Transactional
    public Gesture createGesture(Gesture gesture) {
        // 액션이 없는 경우 기본 액션을 생성
        if (gesture.getActionId() == null) {
            GestureAction newAction = new GestureAction();

            // 제스처 이름에 따라 기본 함수 설정
            if (gesture.getGestureName() != null) {
                if (gesture.getGestureName().contains("엄지") ||
                        gesture.getGestureName().contains("thumbs up")) {
                    newAction.setSelectedFunction(GestureAction.SelectedFunction.FINE_TEXT);
                    newAction.setMessage("괜찮아~");
                } else if (gesture.getGestureName().contains("0")) {
                    newAction.setSelectedFunction(GestureAction.SelectedFunction.BLACK_SCREEN);
                } else if (gesture.getGestureName().contains("손가락")) {
                    newAction.setSelectedFunction(GestureAction.SelectedFunction.PERSON_TEXT);
                    newAction.setMessage("인사하기");
                } else {
                    newAction.setSelectedFunction(GestureAction.SelectedFunction.ALARM);
                }
            } else {
                // 기본값 설정
                newAction.setSelectedFunction(GestureAction.SelectedFunction.ALARM);
            }

            // 액션 저장 및 제스처와 연결
            GestureAction savedAction = gestureActionRepository.save(newAction);
            gesture.setActionId(savedAction.getActionId());
        } else {
            // 기존 액션 ID가 있는 경우 존재하는지 확인
            gestureActionRepository.findById(gesture.getActionId())
                    .orElseThrow(() -> new NoSuchElementException("Gesture action not found"));
        }

        return gestureRepository.save(gesture);
    }
    @Transactional
    public Gesture updateGesture(Gesture gesture) {
        Gesture existingGesture = gestureRepository.findById(gesture.getGestureId())
                .orElseThrow(() -> new NoSuchElementException("Gesture not found"));

        if (gesture.getActionId() != null) {
            gestureActionRepository.findById(gesture.getActionId())
                    .orElseThrow(() -> new NoSuchElementException("Gesture action not found"));
        }

        return gestureRepository.save(gesture);
    }

    @Transactional
    public Gesture associateGestureWithAction(Integer gestureId, Integer actionId) {
        Gesture gesture = gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("Gesture not found"));

        gestureActionRepository.findById(actionId)
                .orElseThrow(() -> new NoSuchElementException("Gesture action not found"));

        gesture.setActionId(actionId);
        return gestureRepository.save(gesture);
    }

    @Transactional
    public Gesture setGestureEnabled(Integer gestureId, String isEnabled) {
        Gesture gesture = gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("Gesture not found"));

        if (!isEnabled.equals("yes") && !isEnabled.equals("no")) {
            throw new IllegalArgumentException("Invalid value for isEnabled. Must be 'yes' or 'no'.");
        }

        gesture.setIsEnabled(isEnabled);
        return gestureRepository.save(gesture);
    }
    @Transactional
    public Gesture selectGestureFunction(Integer gestureId, String selectedFunction) {
        System.out.println("선택된 기능 메서드 호출됨. gestureId=" + gestureId + ", selectedFunction=" + selectedFunction);

        Gesture gesture = gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("Gesture not found"));
        System.out.println("제스처 찾음: " + gesture.getGestureId() + ", 현재 actionId=" + gesture.getActionId());

        GestureAction action;
        if (gesture.getActionId() != null) {
            action = gestureActionRepository.findById(gesture.getActionId())
                    .orElseThrow(() -> new NoSuchElementException("Gesture action not found"));
            System.out.println("기존 액션 찾음: " + action.getActionId());
        } else {
            action = new GestureAction();
            action = gestureActionRepository.save(action);
            gesture.setActionId(action.getActionId());
            System.out.println("새 액션 생성됨: " + action.getActionId());
        }

        try {
            System.out.println("String에서 enum으로 변환 시도: " + selectedFunction);

            // 기능 명칭 맵핑 (클라이언트에서 보낸 문자열 -> enum 값)
            GestureAction.SelectedFunction function;
            switch(selectedFunction) {
                case "BLACK_SCREEN":
                    function = GestureAction.SelectedFunction.BLACK_SCREEN;
                    break;
                case "SIGNAL":
                    function = GestureAction.SelectedFunction.SIGNAL;
                    break;
                case "TIME_CAPTURE":
                    function = GestureAction.SelectedFunction.TIME_CAPTURE;
                    break;
                case "ALARM":
                    function = GestureAction.SelectedFunction.ALARM;
                    break;
                case "FINE_TEXT":
                    function = GestureAction.SelectedFunction.FINE_TEXT;
                    break;
                case "EMERGENCY_TEXT":
                    function = GestureAction.SelectedFunction.EMERGENCY_TEXT;
                    break;
                case "HELP_TEXT":
                    function = GestureAction.SelectedFunction.HELP_TEXT;
                    break;
                case "PERSON_TEXT":
                    function = GestureAction.SelectedFunction.PERSON_TEXT;
                    break;
                default:
                    // 기본값은 ALARM으로 설정
                    function = GestureAction.SelectedFunction.ALARM;
                    break;
            }

            System.out.println("enum으로 변환 성공: " + function);

            // applySelectedFunction 메서드는 각 기능에 맞는 기본 메시지도 설정합니다
            action.setSelectedFunction(function);
            GestureAction savedAction = gestureActionRepository.save(action);
            System.out.println("액션 저장됨: " + savedAction.getActionId());

            // 저장된 액션을 다시 확인
            GestureAction checkedAction = gestureActionRepository.findById(savedAction.getActionId()).orElse(null);
            if (checkedAction != null) {
                System.out.println("저장 후 액션 확인: selectedFunction=" + checkedAction.getSelectedFunction());
                System.out.println("저장 후 액션 필드 값: blackScreen=" + checkedAction.getBlackScreen() +
                        ", sendAlert=" + checkedAction.getSendAlert() +
                        ", capture=" + checkedAction.getCapture() +
                        ", notifications=" + checkedAction.getNotifications() +
                        ", message=" + checkedAction.getMessage());
            }
        } catch (Exception e) {
            System.out.println("함수 설정 중 오류 발생: " + e.getMessage());
            // 오류 발생 시 기본값으로 ALARM 사용
            action.setSelectedFunction(GestureAction.SelectedFunction.ALARM);
            gestureActionRepository.save(action);
        }

        Gesture savedGesture = gestureRepository.save(gesture);
        System.out.println("제스처 저장됨: " + savedGesture.getGestureId() + ", actionId=" + savedGesture.getActionId());

        return savedGesture;
    }

    @Transactional
    public Gesture setGestureMessage(Integer gestureId, String message) {
        Gesture gesture = gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("Gesture not found"));

        if (gesture.getActionId() != null) {
            GestureAction action = gestureActionRepository.findById(gesture.getActionId())
                    .orElseThrow(() -> new NoSuchElementException("Gesture action not found"));

            action.setMessage(message);
            gestureActionRepository.save(action);
        } else {
            // 액션이 없는 경우 새로 생성
            GestureAction newAction = new GestureAction();
            newAction.setMessage(message);
            // 기본값으로 FINE_TEXT 설정 (메시지가 있으므로)
            newAction.setSelectedFunction(GestureAction.SelectedFunction.FINE_TEXT);

            GestureAction savedAction = gestureActionRepository.save(newAction);
            gesture.setActionId(savedAction.getActionId());
            gestureRepository.save(gesture);
        }

        return gesture;
    }

    @Transactional
    public void deleteGesture(Integer gestureId) {
        gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("Gesture not found"));

        gestureRepository.deleteById(gestureId);
    }

    @Transactional
    public Gesture getGestureById(Integer gestureId) {
        return gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("Gesture not found with ID: " + gestureId));
    }

    @Transactional
    public void setGesturesEnabledByUser(Integer userId, String isEnabled) {
        List<Gesture> userGestures = gestureRepository.findByUserId(userId);
        for (Gesture gesture : userGestures) {
            gesture.setIsEnabled(isEnabled);
        }
        gestureRepository.saveAll(userGestures);
    }
    public List<GestureActionDto> getGesturesWithActionsByUserId(Integer userId) {
        List<Gesture> gestures = gestureRepository.findByUserId(userId);
        List<GestureActionDto> result = new ArrayList<>();

        for (Gesture gesture : gestures) {
            GestureActionDto dto = new GestureActionDto();
            dto.setGestureId(gesture.getGestureId());
            dto.setUserId(gesture.getUserId());
            dto.setCameraId(gesture.getCameraId());
            dto.setGestureName(gesture.getGestureName());
            dto.setGestureType(gesture.getGestureType());
            dto.setGestureDescription(gesture.getGestureDescription());
            dto.setGestureImagePath(gesture.getGestureImagePath());
            dto.setIsEnabled(gesture.getIsEnabled());

            if (gesture.getActionId() != null) {
                GestureAction action = gestureActionRepository.findById(gesture.getActionId()).orElse(null);
                if (action != null) {
                    // 액션이 존재하는 경우
                    dto.setSelectedFunction(action.getSelectedFunction());
                    dto.setMessage(action.getMessage());

                    // selectedFunction이 null인 경우 메시지에 따라 설정
                    if (dto.getSelectedFunction() == null && action.getMessage() != null) {
                        String message = action.getMessage();
                        if (message.contains("괜찮아")) {
                            dto.setSelectedFunction(GestureAction.SelectedFunction.FINE_TEXT);
                        } else if (message.contains("도와줘")) {
                            dto.setSelectedFunction(GestureAction.SelectedFunction.EMERGENCY_TEXT);
                        } else if (message.contains("불편해")) {
                            dto.setSelectedFunction(GestureAction.SelectedFunction.HELP_TEXT);
                        } else if (message.contains("인사하기")) {
                            dto.setSelectedFunction(GestureAction.SelectedFunction.PERSON_TEXT);
                        } else {
                            // 기본값
                            dto.setSelectedFunction(GestureAction.SelectedFunction.ALARM);
                        }
                    } else if (dto.getSelectedFunction() == null) {
                        // 메시지도 없는 경우 기본값 설정
                        dto.setSelectedFunction(GestureAction.SelectedFunction.ALARM);
                    }
                }
            }

            result.add(dto);
        }

        return result;
    }

    private void setDefaultFunctionByGestureName(GestureActionDto dto, String gestureName) {
        if (gestureName == null) return;

        if (gestureName.contains("엄지") || gestureName.contains("thumbs up")) {
            dto.setSelectedFunction(GestureAction.SelectedFunction.FINE_TEXT);
            dto.setMessage("괜찮아~");
        } else if (gestureName.contains("0")) {
            dto.setSelectedFunction(GestureAction.SelectedFunction.BLACK_SCREEN);
        } else if (gestureName.contains("손가락")) {
            dto.setSelectedFunction(GestureAction.SelectedFunction.PERSON_TEXT);
            dto.setMessage("인사하기");
        } else {
            // 기본값 설정
            dto.setSelectedFunction(GestureAction.SelectedFunction.ALARM);
        }
    }
    @Transactional
    public void setupMissingActions() {
        List<Gesture> allGestures = gestureRepository.findAll();

        for (Gesture gesture : allGestures) {
            if (gesture.getActionId() == null) {
                // 새 액션 생성
                GestureAction newAction = new GestureAction();

                // 제스처 이름에 따라 기본 함수 설정
                if (gesture.getGestureName() != null) {
                    String gestureName = gesture.getGestureName().toLowerCase();
                    if (gestureName.contains("엄지") || gestureName.contains("thumbs up")) {
                        newAction.setSelectedFunction(GestureAction.SelectedFunction.FINE_TEXT);
                    } else if (gestureName.contains("0")) {
                        newAction.setSelectedFunction(GestureAction.SelectedFunction.BLACK_SCREEN);
                    } else if (gestureName.contains("손가락")) {
                        newAction.setSelectedFunction(GestureAction.SelectedFunction.PERSON_TEXT);
                    } else {
                        newAction.setSelectedFunction(GestureAction.SelectedFunction.ALARM);
                    }
                } else {
                    newAction.setSelectedFunction(GestureAction.SelectedFunction.ALARM);
                }

                // 액션 저장 및 제스처와 연결
                GestureAction savedAction = gestureActionRepository.save(newAction);
                gesture.setActionId(savedAction.getActionId());
                gestureRepository.save(gesture);
            } else {
                // 액션이 있지만 메시지가 없는 경우 업데이트
                GestureAction action = gestureActionRepository.findById(gesture.getActionId()).orElse(null);
                if (action != null && (action.getMessage() == null || action.getMessage().isEmpty())) {
                    // selectedFunction에 맞는 기본 메시지 설정을 위해 다시 적용
                    if (action.getSelectedFunction() != null) {
                        action.applySelectedFunction(action.getSelectedFunction());
                        gestureActionRepository.save(action);
                    }
                }
            }
        }
    }
    public GestureActionDto convertToDto(Gesture gesture) {
        GestureActionDto dto = new GestureActionDto();
        dto.setGestureId(gesture.getGestureId());
        dto.setUserId(gesture.getUserId());
        dto.setCameraId(gesture.getCameraId());
        dto.setGestureName(gesture.getGestureName());
        dto.setGestureType(gesture.getGestureType());
        dto.setGestureDescription(gesture.getGestureDescription());
        dto.setGestureImagePath(gesture.getGestureImagePath());
        dto.setIsEnabled(gesture.getIsEnabled());

        if (gesture.getActionId() != null) {
            GestureAction action = gestureActionRepository.findById(gesture.getActionId()).orElse(null);
            if (action != null) {
                dto.setSelectedFunction(action.getSelectedFunction());
                dto.setMessage(action.getMessage());
            }
        }

        return dto;
    }


}
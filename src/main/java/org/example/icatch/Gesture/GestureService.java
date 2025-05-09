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
        if (gesture.getActionId() != null) {
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
            System.out.println("기존 액션 찾음: " + action.getActionId() + ", 현재 selectedFunction=" + action.getSelectedFunction());
        } else {
            action = new GestureAction();
            action = gestureActionRepository.save(action);
            gesture.setActionId(action.getActionId());
            System.out.println("새 액션 생성됨: " + action.getActionId());
        }

        try {
            System.out.println("String에서 enum으로 변환 시도: " + selectedFunction);
            GestureAction.SelectedFunction function =
                    GestureAction.SelectedFunction.valueOf(selectedFunction);
            System.out.println("enum으로 변환 성공: " + function);

            action.setSelectedFunction(function);
            GestureAction savedAction = gestureActionRepository.save(action);
            System.out.println("액션 저장됨: " + savedAction.getActionId() + ", savedFunction=" + savedAction.getSelectedFunction());

            // 저장된 액션을 다시 확인
            GestureAction checkedAction = gestureActionRepository.findById(savedAction.getActionId()).orElse(null);
            if (checkedAction != null) {
                System.out.println("저장 후 액션 확인: selectedFunction=" + checkedAction.getSelectedFunction());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("잘못된 함수 이름: " + selectedFunction);
            throw new IllegalArgumentException("Invalid function: " + selectedFunction);
        }

        Gesture savedGesture = gestureRepository.save(gesture);
        System.out.println("제스처 저장됨: " + savedGesture.getGestureId() + ", actionId=" + savedGesture.getActionId());

        return savedGesture;
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
                    dto.setSelectedFunction(action.getSelectedFunction());
                    dto.setMessage(action.getMessage());
                }
            }

            result.add(dto);
        }

        return result;
    }

}
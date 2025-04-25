package org.example.icatch.service;

import org.example.icatch.dto.GestureActionDto;
import org.example.icatch.model.Gesture;
import org.example.icatch.model.GestureAction;
import org.example.icatch.repository.GestureActionRepository;
import org.example.icatch.repository.GestureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GestureService {
    private static final Logger logger = LoggerFactory.getLogger(GestureService.class);

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

    public Gesture findById(Integer gestureId) {
        return gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("제스처를 찾을 수 없습니다: " + gestureId));
    }

    @Transactional
    public Gesture createGesture(Gesture gesture) {
        if (gesture.getActionId() != null) {
            gestureActionRepository.findById(gesture.getActionId())
                    .orElseThrow(() -> new NoSuchElementException("제스처 액션을 찾을 수 없습니다"));
        }

        return gestureRepository.save(gesture);
    }

    @Transactional
    public Gesture updateGesture(Gesture gesture) {
        Gesture existingGesture = gestureRepository.findById(gesture.getGestureId())
                .orElseThrow(() -> new NoSuchElementException("제스처를 찾을 수 없습니다"));

        if (gesture.getActionId() != null) {
            gestureActionRepository.findById(gesture.getActionId())
                    .orElseThrow(() -> new NoSuchElementException("제스처 액션을 찾을 수 없습니다"));
        }

        return gestureRepository.save(gesture);
    }

    @Transactional
    public Gesture associateGestureWithAction(Integer gestureId, Integer actionId) {
        Gesture gesture = gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("제스처를 찾을 수 없습니다"));

        gestureActionRepository.findById(actionId)
                .orElseThrow(() -> new NoSuchElementException("제스처 액션을 찾을 수 없습니다"));

        gesture.setActionId(actionId);
        return gestureRepository.save(gesture);
    }

    @Transactional
    public Gesture setGestureEnabled(Integer gestureId, String isEnabled) {
        Gesture gesture = gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("제스처를 찾을 수 없습니다"));

        if (!isEnabled.equals("yes") && !isEnabled.equals("no")) {
            throw new IllegalArgumentException("isEnabled 값이 잘못되었습니다. 'yes' 또는 'no'여야 합니다.");
        }

        gesture.setIsEnabled(isEnabled);
        return gestureRepository.save(gesture);
    }

    @Transactional
    public Gesture selectGestureFunction(Integer gestureId, String selectedFunction) {
        Gesture gesture = gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("제스처를 찾을 수 없습니다"));

        GestureAction action;
        if (gesture.getActionId() != null) {
            action = gestureActionRepository.findById(gesture.getActionId())
                    .orElseThrow(() -> new NoSuchElementException("제스처 액션을 찾을 수 없습니다"));
        } else {
            action = new GestureAction();
            action = gestureActionRepository.save(action);
            gesture.setActionId(action.getActionId());
        }

        try {
            GestureAction.SelectedFunction function =
                    GestureAction.SelectedFunction.valueOf(selectedFunction);
            action.setSelectedFunction(function);
            gestureActionRepository.save(action);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 기능: " + selectedFunction);
        }

        return gestureRepository.save(gesture);
    }

    @Transactional
    public void deleteGesture(Integer gestureId) {
        gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("제스처를 찾을 수 없습니다"));

        gestureRepository.deleteById(gestureId);
    }

    @Transactional
    public Gesture createGestureWithAction(GestureActionDto dto) {
        if (dto.getUserId() == null || dto.getCameraId() == null || dto.getGestureName() == null) {
            throw new IllegalArgumentException("사용자 ID, 카메라 ID, 제스처 이름은 필수 입력값입니다.");
        }

        GestureAction action = new GestureAction();
        action.setSelectedFunction(dto.getSelectedFunction());
        action.setMessage(dto.getMessage());
        action = gestureActionRepository.save(action);

        Gesture gesture = new Gesture();
        gesture.setUserId(dto.getUserId());
        gesture.setCameraId(dto.getCameraId());
        gesture.setGestureName(dto.getGestureName());
        gesture.setGestureType(dto.getGestureType());
        gesture.setGestureDescription(dto.getGestureDescription());
        gesture.setGestureImagePath(dto.getGestureImagePath());
        gesture.setIsEnabled(dto.getIsEnabled() != null ? dto.getIsEnabled() : "yes");
        gesture.setActionId(action.getActionId());

        return gestureRepository.save(gesture);
    }

    @Transactional
    public Gesture updateGestureWithAction(Integer gestureId, GestureActionDto dto) {
        if (gestureId == null) {
            throw new IllegalArgumentException("제스처 ID는 필수 입력값입니다.");
        }

        Gesture existingGesture = gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("제스처를 찾을 수 없습니다"));

        GestureAction action = gestureActionRepository.findById(existingGesture.getActionId())
                .orElseThrow(() -> new NoSuchElementException("제스처 액션을 찾을 수 없습니다"));

        if (dto.getSelectedFunction() != null) action.setSelectedFunction(dto.getSelectedFunction());
        if (dto.getMessage() != null) action.setMessage(dto.getMessage());
        gestureActionRepository.save(action);

        if (dto.getUserId() != null) existingGesture.setUserId(dto.getUserId());
        if (dto.getCameraId() != null) existingGesture.setCameraId(dto.getCameraId());
        if (dto.getGestureName() != null) existingGesture.setGestureName(dto.getGestureName());
        if (dto.getGestureType() != null) existingGesture.setGestureType(dto.getGestureType());
        if (dto.getGestureDescription() != null) existingGesture.setGestureDescription(dto.getGestureDescription());
        if (dto.getGestureImagePath() != null) existingGesture.setGestureImagePath(dto.getGestureImagePath());
        if (dto.getIsEnabled() != null) existingGesture.setIsEnabled(dto.getIsEnabled());

        return gestureRepository.save(existingGesture);
    }

    public GestureActionDto getGestureWithAction(Integer gestureId) {
        Gesture gesture = gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("제스처를 찾을 수 없습니다"));

        GestureAction action = gestureActionRepository.findById(gesture.getActionId())
                .orElseThrow(() -> new NoSuchElementException("제스처 액션을 찾을 수 없습니다"));

        return convertToDTO(gesture, action);
    }

    public List<GestureActionDto> getGesturesWithActionsByUserId(Integer userId) {
        List<Gesture> gestures = gestureRepository.findByUserId(userId);
        List<GestureActionDto> result = new ArrayList<>();

        for (Gesture gesture : gestures) {
            try {
                GestureAction action = gestureActionRepository.findById(gesture.getActionId())
                        .orElseThrow(() -> new NoSuchElementException("제스처 액션을 찾을 수 없습니다: " + gesture.getActionId()));

                result.add(convertToDTO(gesture, action));
            } catch (Exception e) {
                logger.error("액션 ID {}의 제스처 처리 중 오류 발생: {}", gesture.getActionId(), e.getMessage());
            }
        }

        return result;
    }

    private GestureActionDto convertToDTO(Gesture gesture, GestureAction action) {
        GestureActionDto dto = new GestureActionDto();
        dto.setGestureId(gesture.getGestureId());
        dto.setUserId(gesture.getUserId());
        dto.setCameraId(gesture.getCameraId());
        dto.setGestureName(gesture.getGestureName());
        dto.setGestureType(gesture.getGestureType());
        dto.setGestureDescription(gesture.getGestureDescription());
        dto.setGestureImagePath(gesture.getGestureImagePath());
        dto.setIsEnabled(gesture.getIsEnabled());
        dto.setSelectedFunction(action.getSelectedFunction());
        dto.setMessage(action.getMessage());
        return dto;
    }
}
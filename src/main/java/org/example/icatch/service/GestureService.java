package org.example.icatch.service;

import org.example.icatch.model.Gesture;
import org.example.icatch.model.GestureAction;
import org.example.icatch.repository.GestureActionRepository;
import org.example.icatch.repository.GestureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        // Validate that referenced entities exist
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
        Gesture gesture = gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("Gesture not found"));

        GestureAction action;
        if (gesture.getActionId() != null) {
            action = gestureActionRepository.findById(gesture.getActionId())
                    .orElseThrow(() -> new NoSuchElementException("Gesture action not found"));
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
            throw new IllegalArgumentException("Invalid function: " + selectedFunction);
        }

        return gestureRepository.save(gesture);
    }

    @Transactional
    public void deleteGesture(Integer gestureId) {
        gestureRepository.findById(gestureId)
                .orElseThrow(() -> new NoSuchElementException("Gesture not found"));

        gestureRepository.deleteById(gestureId);
    }
}
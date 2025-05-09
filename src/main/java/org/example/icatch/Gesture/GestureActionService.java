package org.example.icatch.Gesture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GestureActionService {

    private final GestureActionRepository gestureActionRepository;

    @Autowired
    public GestureActionService(GestureActionRepository gestureActionRepository) {
        this.gestureActionRepository = gestureActionRepository;
    }

    public List<GestureAction> getAllGestureActions() {
        return gestureActionRepository.findAll();
    }

    public GestureAction getGestureActionById(Integer actionId) {
        return gestureActionRepository.findById(actionId)
                .orElseThrow(() -> new NoSuchElementException("Gesture action not found with ID: " + actionId));
    }

    @Transactional
    public GestureAction createGestureAction(GestureAction gestureAction) {
        return gestureActionRepository.save(gestureAction);
    }

    @Transactional
    public GestureAction updateGestureAction(GestureAction gestureAction) {
        gestureActionRepository.findById(gestureAction.getActionId())
                .orElseThrow(() -> new NoSuchElementException("Gesture action not found"));

        return gestureActionRepository.save(gestureAction);
    }

    @Transactional
    public void deleteGestureAction(Integer actionId) {
        gestureActionRepository.findById(actionId)
                .orElseThrow(() -> new NoSuchElementException("Gesture action not found"));

        gestureActionRepository.deleteById(actionId);
    }

    @Transactional
    public GestureAction setSelectedFunction(Integer actionId, GestureAction.SelectedFunction function) {
        GestureAction action = getGestureActionById(actionId);
        action.setSelectedFunction(function);
        return gestureActionRepository.save(action);
    }

    @Transactional
    public GestureAction setMessage(Integer actionId, String message) {
        GestureAction action = getGestureActionById(actionId);
        action.setMessage(message);
        return gestureActionRepository.save(action);
    }
    public List<GestureAction> findByFunction(GestureAction.SelectedFunction function) {
        if (function == null) {
            return Collections.emptyList();
        }

        switch (function) {
            case BLACK_SCREEN:
                return gestureActionRepository.findByBlackScreen(GestureAction.EnabledStatus.enabled);
            case SIGNAL:
                return gestureActionRepository.findBySendAlert(GestureAction.EnabledStatus.enabled);
            case TIME_CAPTURE:
                return gestureActionRepository.findByCapture(GestureAction.EnabledStatus.enabled);
            case ALARM:
                return gestureActionRepository.findByNotifications(GestureAction.EnabledStatus.enabled);
            default:
                return Collections.emptyList();
        }
    }
}
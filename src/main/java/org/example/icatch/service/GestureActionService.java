package org.example.icatch.service;

import jakarta.transaction.Transactional;
import org.example.icatch.model.GestureAction;
import org.example.icatch.repository.GestureActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GestureActionService{

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
        // Check if gesture action exists
        gestureActionRepository.findById(gestureAction.getActionId())
                .orElseThrow(() -> new NoSuchElementException("Gesture action not found"));

        return gestureActionRepository.save(gestureAction);
    }

    @Transactional
    public void deleteGestureAction(Integer actionId) {
        // Check if gesture action exists
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
}
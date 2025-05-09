package org.example.icatch.Gesture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GestureActionRepository extends JpaRepository<GestureAction, Integer> {

    List<GestureAction> findByBlackScreen(GestureAction.EnabledStatus status);
    List<GestureAction> findBySendAlert(GestureAction.EnabledStatus status);
    List<GestureAction> findByCapture(GestureAction.EnabledStatus status);
    List<GestureAction> findByNotifications(GestureAction.EnabledStatus status);
}
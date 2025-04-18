package org.example.icatch.repository;

import org.example.icatch.model.Gesture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GestureRepository extends JpaRepository<Gesture, Integer> {

    List<Gesture> findByUserId(Integer userId);

    List<Gesture> findByCameraId(Integer cameraId);

    List<Gesture> findByUserIdAndCameraId(Integer userId, Integer cameraId);

    List<Gesture> findByIsEnabled(String isEnabled);
}
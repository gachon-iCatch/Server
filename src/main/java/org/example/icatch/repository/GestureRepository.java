package org.example.icatch.repository;

import org.example.icatch.model.Camera;
import org.example.icatch.model.Gesture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GestureRepository extends JpaRepository<Gesture, Integer> {
    List<Gesture> findByUserId(Integer userId);
    Integer countByUserId(Integer userId);
    List<Gesture> findByCameraId(Integer cameraId);
}


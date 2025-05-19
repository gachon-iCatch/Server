package org.example.icatch.Gesture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GestureRepository extends JpaRepository<Gesture, Integer> {

    List<Gesture> findByUserId(Integer userId);

    List<Gesture> findByCameraId(Integer cameraId);
    long countByUserId(Integer userId);


    List<Gesture> findByUserIdAndCameraId(Integer userId, Integer cameraId);

    List<Gesture> findByIsEnabled(String isEnabled);

}
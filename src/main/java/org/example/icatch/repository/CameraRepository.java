package org.example.icatch.repository;

import org.example.icatch.model.Camera;
import org.example.icatch.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Integer> {
    List<Camera> findByUserId_UserId(Integer userId);
    List<Camera> findByTargetId(Integer targetId);
}

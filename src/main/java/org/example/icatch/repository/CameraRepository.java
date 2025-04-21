package org.example.icatch.repository;

import org.example.icatch.model.Camera;
import org.example.icatch.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Integer> {
    List<Camera> findByUser_UserId(Integer userId);
    List<Camera> findByTargetId(Integer targetId);
    Optional<Camera> findFirstByUser_UserIdOrderByCreatedAtDesc(Integer userId);
}

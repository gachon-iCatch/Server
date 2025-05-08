package org.example.icatch.Camera;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Integer> {
    // 수정
    List<Camera> findByUserId_UserId(Integer userId);

    Integer countByUserId_UserId(Integer userId);

    Optional<Camera> findFirstByUserId_UserIdOrderByCreatedAtDesc(Integer userId);
}
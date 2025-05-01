package org.example.icatch.Camera;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Integer> {
    // 수정
    List<Camera> findByUser_UserId(Integer userId);

    Integer countByUser_UserId(Integer userId);
}
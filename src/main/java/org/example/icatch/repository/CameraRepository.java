package org.example.icatch.repository;

import org.example.icatch.model.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CameraRepository extends JpaRepository<Camera, Integer> {
    // userId가 현재 User 타입 객체이므로, 이렇게 접근해야 합니다
    List<Camera> findByUser_UserId(Integer userId);
    Integer countByUser_UserId(Integer userId);
}
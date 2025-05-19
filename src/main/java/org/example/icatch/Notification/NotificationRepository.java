package org.example.icatch.Notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    List<Notification> findByUserUserIdOrderByCreatedAtDesc(Integer userId);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.userId = :userId")
    int countByUserId(@Param("userId") Integer userId);

    // 카메라 ID로 알림 삭제 (대신 카메라와 관련된 모든 알림을 삭제)
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM notification WHERE camera_id = :cameraId", nativeQuery = true)
    void deleteByCameraId(@Param("cameraId") Integer cameraId);
}